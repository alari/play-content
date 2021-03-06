package infra.piece.quote

import infra.piece.core.{Piece, PieceKind}
import play.api.{Mode, Plugin}
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import scala.concurrent.{Future, ExecutionContext}
import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import org.pegdown.{Extensions, PegDownProcessor}
import play.api.libs.concurrent.Akka
import akka.util.Timeout
import org.apache.commons.validator.routines.UrlValidator
import akka.pattern.ask

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:58
 */
class QuoteKind(implicit app: play.api.Application) extends PieceKind("quote") with Plugin{
  override type P = QuotePiece

  private var processor: ActorRef = _

  override def onStart() {
    super.onStart()
    try {
      processor = Akka.system.actorOf(Props[MarkdownActor])
    } catch {
      case e: IllegalStateException if app.mode == Mode.Test =>
        play.api.Logger.info("Creating an actor while shutting akka system down", e)
    }
  }

  override def onStop() {
    super.onStop()
    if(processor != null) {
      processor ! PoisonPill
      processor = null
    }
  }

  implicit val timeout = {
    import scala.concurrent.duration._
    Timeout(1 second)
  }

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P if p.source.exists(_.trim.length > 0) =>
      (processor ? p.source.get).mapTo[String].map(t => p.copy(
        processed = Some(t),
        originalHref = p.originalHref.filter(UrlValidator.getInstance().isValid))
      )
  }

  override def html(piece: P): Html = infra.piece.quote.html.quote(piece)

  override val format: Format[P] = Json.format[P]
}

class MarkdownActor extends Actor {
  val Processor = new PegDownProcessor(Extensions.ALL)

  override def receive: Receive = {
    case s: String => sender ! Processor.markdownToHtml(s)
  }
}