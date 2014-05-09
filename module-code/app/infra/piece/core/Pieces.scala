package infra.piece.core

import play.api.mvc.MultipartFormData
import play.api.libs.Files
import scala.concurrent.{Future, ExecutionContext}
import java.net.{MalformedURLException, URL}
import infra.wished.Unwished

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 13:40
 */
object Pieces {
  type File = MultipartFormData.FilePart[Files.TemporaryFile]

  def kinds(implicit app: play.api.Application): Map[String, PieceKind] =
    app.plugins.view.map {
      case p: PieceKind => Some(p.name -> p)
      case _ => None
    }.filter(_.isDefined).map(_.get).force.toMap

  def fileStorage(implicit app: play.api.Application): FileStorage =
    app.plugin[FileStorage].getOrElse(throw new RuntimeException("FileStorage plugin not found"))

  def withKind[T](kind: String)(f: PieceKind => T)(implicit app: play.api.Application) =
    kinds.get(kind).map(f).getOrElse {
      throw new RuntimeException(s"Piece kind not found: $kind, probably a plugin must be enabled")
    }

  def tryHandle[A,R](a: A, fs: List[PartialFunction[A,Future[R]]])(implicit ec: ExecutionContext): Future[R] =
    fs match {
      case f :: fss if f.isDefinedAt(a) =>
        f.apply(a) recoverWith {
          case e =>
            tryHandle(a, fss)
        }

      case f :: fss =>
        tryHandle(a, fss)

      case Nil =>
        Future.failed(Unwished.UnprocessableEntity)
    }

  def handleUrl(url: URL)(implicit app: play.api.Application, ec: ExecutionContext): Future[Piece] =
    tryHandle(url, kinds.values.map(_.handleUrl).toList)

  def handleUrlString(stringUrl: String)(implicit app: play.api.Application, ec: ExecutionContext): Future[Piece] =
    try {
      val url = new URL(if (stringUrl.startsWith("http:") || stringUrl.startsWith("https:")) stringUrl else "http://" + stringUrl)
      handleUrl(url)
    } catch {
      case e: MalformedURLException =>
        Future.failed(Unwished.BadRequest("Malformed URL"))
    }

  def handlePiece(piece: Piece)(implicit app: play.api.Application, ec: ExecutionContext): Future[Piece] =
    tryHandle(piece, kinds.values.map(_.handlePiece).toList)

  def handleFile(userId: String, file: File)(implicit app: play.api.Application, ec: ExecutionContext): Future[Piece] =
    tryHandle(file, kinds.values.map(_.handleFile(userId)).toList)

}
