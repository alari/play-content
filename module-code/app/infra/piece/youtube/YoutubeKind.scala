package infra.piece.youtube

import infra.piece.core.{Piece, PieceKind}
import play.api.Plugin
import play.api.libs.json.{Json, Format}
import play.api.templates.Html
import play.api.libs.ws.WS
import java.net.URL
import org.apache.http.client.utils.URLEncodedUtils
import scala.concurrent.{Future, ExecutionContext}

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 14:31
 */
class YoutubeKind(implicit app: play.api.Application) extends PieceKind("youtube") with Plugin {
  override type P = YoutubePiece

  override def html(piece: P): Html = infra.piece.youtube.html.youtube(piece)

  override val format: Format[P] = Json.format[P]

  override def getThumbUrl(piece: P): Option[String] = piece.thumbs.flatMap(_.get("default"))

  override def handleUrl(implicit ec: ExecutionContext): PartialFunction[URL, Future[P]] = {
    case u if extractCode(u).isDefined =>
      for {
        Some(pp) <- handleCode(extractCode(u).get)
      } yield pp
  }

  override def handlePiece(implicit ec: ExecutionContext): PartialFunction[Piece, Future[P]] = {
    case p: P =>
      for {
        Some(pp) <- handleCode(p.code)
      } yield pp
  }

  def handleCode(code: String)(implicit ec: ExecutionContext) =
    WS.url(s"https://gdata.youtube.com/feeds/api/videos/$code?v=2").get().map {
    response =>
      if (response.status == 200) {
        val e = response.xml

        val title = (e \ "title").text
        val description = (e \ "group" \ "description").text
        val thumbs = (e \\ "thumbnail").foldLeft(Map[String, String]()) {
          case (map, thumb) =>
            thumb.attribute("http://gdata.youtube.com/schemas/2007", "name") match {
              case Some(name) =>
                map + (name.toString -> (thumb \ "@url").text)
              case None =>
                map
            }
        }

        Some(YoutubePiece(
          code,
          if (title.size > 0) Some(title) else None,
          if (description.size > 0) Some(description) else Some(e.toString()),
          if (thumbs.size > 0) Some(thumbs) else None
        ))
      } else {
        None
      }
  }

  def extractCode(url: URL): Option[String] = if (url.getHost == "youtu.be") {
    // http://youtu.be/zi3AqicZgEk
    Some(url.getPath.substring(1))
  } else if (url.getHost == "www.youtube.com" && url.getPath == "/watch") {
    import scala.collection.JavaConversions._
    // http://www.youtube.com/watch?v=zi3AqicZgEk&feature=g-logo&context=G2e33cabFOAAAAAAABAA
    URLEncodedUtils.parse(url.toURI, "UTF-8").find(_.getName == "v").map(_.getValue)
  } else None
}
