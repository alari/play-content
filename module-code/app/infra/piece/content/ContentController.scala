package infra.piece.content

import play.api.libs.json.{Reads, JsError, JsSuccess, JsValue}
import scala.concurrent.{Future, ExecutionContext}
import play.api.mvc.Request
import infra.wished.Unwished
import play.api.Play.current

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 16:25
 */
trait ContentController {

  def handleContent(json: JsValue)(implicit executionContext: ExecutionContext): Future[ContentForm] =
    ContentForm.format.reads(json) match {
      case JsSuccess(value, _) =>
        Content.handle(value)
      case e: JsError =>
        throw Unwished.BadRequest(JsError.toFlatJson(e))
    }

  def handleContent(implicit executionContext: ExecutionContext, request: Request[JsValue]): Future[ContentForm] =
    handleContent(request.body)

  def handleContentOpt(json: JsValue)(implicit executionContext: ExecutionContext): Future[Option[ContentForm]] =
    ContentForm.format.reads(json) match {
      case JsSuccess(value, _) =>
        Content.handle(value).map(Some.apply).recover {
          case e => None
        }
      case e: JsError =>
        Future(None)
    }

  def handleContentOpt(implicit executionContext: ExecutionContext, request: Request[JsValue]): Future[Option[ContentForm]] =
    handleContentOpt(request.body)
}
