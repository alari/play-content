package infra.piece.core

import play.api.Plugin
import play.api.libs.json.Format
import play.api.templates.Html
import scala.concurrent.{ExecutionContext, Future}
import java.net.URL

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 0:05
 */
abstract class PieceKind(val name: String) {
  self: Plugin =>

  type P <: Piece

  def getThumbUrl(piece: P): Option[String] = None

  val format: Format[P]

  def html(piece: P): Html

  def handleUrl(implicit ec: ExecutionContext): PartialFunction[URL, Future[P]] = PartialFunction.empty

  def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]]

  def handleFile(userId: String)(implicit ec: ExecutionContext): PartialFunction[Pieces.File, Future[P]] = PartialFunction.empty
}
