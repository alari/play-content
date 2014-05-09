package infra.piece.html

import infra.piece.core.{Piece, PieceKind}
import play.api.Plugin
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import scala.concurrent.{Future, ExecutionContext}
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 14:45
 */
class HtmlKind(app: play.api.Application) extends PieceKind("html") with Plugin{
  override type P = HtmlPiece

  override def html(piece: P): Html = infra.piece.html.html.html(piece)

  override val format: Format[P] = Json.format[P]

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P => Future(p.copy(content = Jsoup.clean(p.content, Whitelist.relaxed)))
  }
}
