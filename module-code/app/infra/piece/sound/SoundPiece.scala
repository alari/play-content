package infra.piece.sound

import infra.piece.core.Piece

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 14:33
 */
case class SoundPiece(title: Option[String],
                      fileId: String,
                      filename: String,
                      fileIds: Seq[String],
                      id: Option[String] = Piece.genSomeId(),
                      kind: String = "sound") extends Piece
