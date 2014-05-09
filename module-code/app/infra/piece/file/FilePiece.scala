package infra.piece.file

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:22
 */
case class FilePiece(fileId: String,
                     filename: String,
                     title: Option[String],
                     fileIds: Seq[String],
                     id: Option[String] = Piece.genSomeId(),
                     kind: String = "file") extends Piece