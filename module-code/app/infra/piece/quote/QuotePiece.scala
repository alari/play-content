package infra.piece.quote

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:57
 */
case class QuotePiece(source: Option[String],
                      processed: Option[String],
                      title: Option[String],
                      originalTitle: Option[String],
                      originalHref: Option[String],
                      id: Option[String] = Piece.genSomeId(),
                      kind: String = "quote"
                       ) extends Piece