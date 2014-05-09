package infra.piece.image

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 13:40
 */
case class ImagePiece(title: Option[String],
                      filename: String,
                      sizes: Map[String, ImageSize],
                      fileIds: Seq[String],
                      id: Option[String] = Piece.genSomeId(),
                      kind: String = "image") extends Piece
