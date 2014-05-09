package infra.piece.content

import infra.piece.core.Piece
import play.api.libs.json.Json

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 16:08
 */
case class ContentForm(pieces: Seq[Piece]) extends Content{
  override def copyWithPieces(pieces: Seq[Piece]): this.type = copy(pieces = pieces).asInstanceOf[this.type]
}

object ContentForm {
  implicit val format = Json.format[ContentForm]
}