package infra.piece.youtube

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 07.05.14 14:29
 */
case class YoutubePiece(code: String,
                        title: Option[String],
                        description: Option[String],
                        thumbs: Option[Map[String, String]],
                        id: Option[String] = Piece.genSomeId(),

                        kind: String = "youtube") extends Piece