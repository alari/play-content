package infra.piece.content

import infra.piece.core.{Pieces, Piece}
import scala.concurrent.{ExecutionContext, Future}
import play.api.templates.Html

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 16:06
 */
trait Content {

  def pieces: Seq[Piece]

  def copyWithPieces(pieces: Seq[Piece]): this.type

  def html: Html = pieces.map(_.html).fold(Html(""))(_ += _)
}

object Content {
  def handle[T <: Content](content: T)(implicit app: play.api.Application, ec: ExecutionContext): Future[T] = {
    Future.sequence(content.pieces.map(Pieces.handlePiece).map(_.map(Some.apply).recover {
      case _ => None
    }))
      .map(_.filter(_.isDefined).map(_.get))
      .withFilter(_.nonEmpty)
      .map(pcs => content.copyWithPieces(pcs))
  }
}