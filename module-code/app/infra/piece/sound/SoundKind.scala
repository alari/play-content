package infra.piece.sound

import play.api.Plugin
import infra.piece.core.{Pieces, Piece, PieceKind}
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import scala.concurrent.{Future, ExecutionContext}
import infra.piece.core.Pieces.File

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:34
 */
class SoundKind(implicit app: play.api.Application) extends PieceKind("sound") with Plugin{
  override type P = SoundPiece

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P => Future.successful(p)
  }

  override def html(piece: P): Html = infra.piece.sound.html.sound(piece)

  override val format: Format[P] = Json.format[P]

  override def handleFile(userId: String)(implicit ec: ExecutionContext): PartialFunction[File, Future[P]] = {
    case file if file.contentType.exists(t => t == "audio/mpeg" || t == "audio/mp3") && file.filename.endsWith(".mp3") =>
      Pieces.fileStorage
        .store(file.copy(contentType = Some("audio/mpeg")), userId)
        .map(fid => SoundPiece(None, fid, file.filename, Seq(fid)))
  }
}
