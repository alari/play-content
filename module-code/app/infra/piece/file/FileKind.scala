package infra.piece.file

import infra.piece.core.{Pieces, Piece, PieceKind}
import play.api.Plugin
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import scala.concurrent.{Future, ExecutionContext}
import infra.piece.core.Pieces.File

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:27
 */
class FileKind(implicit app: play.api.Application) extends PieceKind("file") with Plugin{
  override type P = FilePiece

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P => Future.successful(p)
  }

  override def html(piece: P): Html = infra.piece.file.html.file(piece)

  override val format: Format[P] = Json.format[P]

  override def handleFile(userId: String)(implicit ec: ExecutionContext): PartialFunction[File, Future[P]] = {
    case f =>
      Pieces.fileStorage.store(f, userId).map(fid => FilePiece(fid, f.filename, Some(f.filename), Seq(fid)))
  }
}
