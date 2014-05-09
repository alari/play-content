package infra.piece.text

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:45
 */
case class TextPiece(source: Option[String],
                     processed: Option[String],
                     title: Option[String],
                     engine: String = "markdown",
                     id: Option[String] = Piece.genSomeId(),
                     kind: String = "text"
                      ) extends Piece