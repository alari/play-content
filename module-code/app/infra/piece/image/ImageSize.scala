package infra.piece.image

/**
 * @author alari (name.alari@gmail.com)
 * @since 08.05.14 13:47
 */
case class ImageSize(width: Int, height: Int, fileId: String) {
  def isLessThen(size: (Int, Int, Double)) = width <= size._1 && height <= size._2
}
