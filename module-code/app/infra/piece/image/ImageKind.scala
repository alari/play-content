package infra.piece.image

import infra.piece.core.{Pieces, Piece, PieceKind}
import play.api.Plugin
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import scala.concurrent.{Future, ExecutionContext}
import infra.piece.core.Pieces.File

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 13:43
 */
class ImageKind(implicit app: play.api.Application) extends PieceKind("image") with Plugin {
  override type P = ImagePiece

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P => Future.successful(p)
  }

  override def html(piece: P): Html = infra.piece.image.html.image(piece)

  implicit val s = Json.format[ImageSize]

  override val format: Format[P] = Json.format[P]

  import ImageProcessor.{Format, Size}

  val sizes = Map[String, Format](
    "max" -> Format(Size(4096, 4096), 1.0),

    "large2048" -> Format(Size(2048, 2048), 0.8),
    "large1600" -> Format(Size(1600, 1600), 0.85),
    "large1200" -> Format(Size(1200, 1200), 0.9),

    "medium800" -> Format(Size(800, 800), 0.9),
    "medium640" -> Format(Size(640, 640), 0.9),
    "medium500" -> Format(Size(500, 500), 0.9),

    "small320" -> Format(Size(320, 320), 0.8),
    "small240" -> Format(Size(240, 240), 0.8),

    "thumbnail" -> Format(Size(100, 100), 0.8)
  )

  override def handleFile(userId: String)(implicit ec: ExecutionContext): PartialFunction[File, Future[P]] = {
    case file if file.contentType.exists(_.startsWith("image/")) =>
      ImageProcessor.resize(file.ref.file, sizes, s => Pieces.fileStorage.store(s.image, file.filename, Some("image/jpeg"), userId)).map {
        images =>
          ImagePiece(Some(file.filename), file.filename, images.mapValues(f => ImageSize(f.width, f.height, f.id)), images.values.map(_.id).toSeq.distinct)
      }
  }

  override def getThumbUrl(piece: P): Option[String] =
    piece.sizes.find(ns => ns._2.isLessThen((300, 300, 1))).map(
      s => Pieces.fileStorage.url(s._2.fileId)
    )

}
