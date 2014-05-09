package infra.piece.core

import scala.concurrent.{Future, ExecutionContext}
import java.io.InputStream
import play.api.mvc.{SimpleResult, Result}

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 13:47
 */
trait FileStorage {
  def store(file: Pieces.File, userId: String)(implicit ec: ExecutionContext): Future[String]

  def store(is: InputStream, filename: String, contentType: Option[String], userId: String)(implicit ec: ExecutionContext): Future[String]

  def url(fileId: String): String

  def download(fileId: String)(implicit ec: ExecutionContext): Future[SimpleResult]
}
