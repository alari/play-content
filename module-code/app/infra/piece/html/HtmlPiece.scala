package infra.piece.html

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 14:45
 */
case class HtmlPiece(
                      content: String,
                      title: Option[String],
                      id: Option[String] = Piece.genSomeId(),
                      kind: String = "html"
                      ) extends Piece