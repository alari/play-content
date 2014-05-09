package infra.piece.core

import java.util.UUID
import play.api.libs.json.{JsError, Reads, Writes, __}
import play.api.Play.current
import play.api.templates.Html

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 0:03
 */
trait Piece {
  def id: Option[String]

  def title: Option[String]

  def kind: String

  def html: Html =
    Pieces.withKind(kind)(k => k.html(this.asInstanceOf[k.P]))

  def thumbUrl: Option[String] =
    Pieces.kinds(current).get(kind).flatMap(k => k.getThumbUrl(this.asInstanceOf[k.P]))
}

object Piece {
  def genSomeId(): Option[String] = Some(UUID.randomUUID().toString)

  implicit val writes = Writes[Piece] {
    p =>
      Pieces.withKind(p.kind) {
        k =>
          k.format.writes(p.asInstanceOf[k.P])
      }
  }
  implicit val reads = Reads[Piece] {
    p =>
      (p \ "kind").asOpt[String] match {
        case Some(k) =>
          Pieces.withKind(k)(_.format.reads(p))
        case None =>
          JsError(__ \ "kind", "Kind is expected for every piece")
      }
  }
}