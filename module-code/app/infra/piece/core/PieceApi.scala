package infra.piece.core

import play.api.mvc.{Action, Request, ActionBuilder, Controller}
import scala.concurrent.Future
import play.api.libs.json.Json
import infra.wished.Unwished
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * @author alari (name.alari@gmail.com)
 * @since 09.05.14 16:47
 */
trait PieceApi[T[A] <: Request[A]] {
  self: Controller =>

  def action: ActionBuilder[T]

  def canUpload(implicit request: T[_]): Future[Boolean]

  def userId(implicit request: T[_]): String

  def parseLink = action.async(parse.json(512)) {
    implicit request =>
      (request.body \ "url").asOpt[String].fold(Future.successful(BadRequest("URL not provided")))(url =>
        for {
          piece <- Pieces.handleUrlString(url)
        } yield Ok(Json.toJson(piece)))
  }

  def upload = action.async(parse.multipartFormData) {
    implicit request =>
      request.body.file("file").map {
        filePart =>
          canUpload(request).flatMap {
            case true =>
              Pieces.handleFile(userId(request), filePart).map(p => Ok(Json.toJson(p)))
            case false =>
              throw Unwished.InsufficientStorage
          }
      }.getOrElse {
        throw Unwished.BadRequest(request.body.files.map(_.key).mkString("\n"))
      }
  }

  def download(fileId: String) = Action.async {
    request =>
      request.headers.get(ETAG) match {
        case Some(`fileId`) =>
          Future successful NotModified
        case _ =>
          Pieces.fileStorage.download(fileId).map(_.withHeaders(
            CACHE_CONTROL -> "no-transform,public,max-age=36000,s-maxage=86400",
            ETAG -> fileId
          ))
      }

  }
}
