package infra.piece.image

import java.io.{IOException, ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import scala.concurrent._
import infra.wished.Unwished
import java.awt.image.BufferedImage
import net.coobird.thumbnailator.Thumbnails
import scala.Some
import javax.imageio.ImageIO


/**
 * @author alari (name.alari@gmail.com)
 * @since 10.01.14 0:21
 */
object ImageProcessor {

  trait SizeWrap {
    def width: Int

    def height: Int

    def >(that: SizeWrap): Boolean = width * height - that.width * that.height > 0

    def <(that: SizeWrap): Boolean = !this.>(that)
  }

  case class Size(width: Int, height: Int) extends SizeWrap

  case class Format(size: Size, quality: Double) extends SizeWrap {
    def width = size.width

    def height = size.height

    def shape(to: SizeWrap): Format =
      if (width <= to.width && height <= to.height) this
      else {
        if (to.width / width > to.height / height) {
          // resizing by x
          copy(size = Size(width, Math.round(to.height * width / to.width)))
        } else {
          copy(size = Size(Math.round(to.width * height / to.height), height))
        }
      }

    def toTuple = (width, height, quality)
  }

  case class File(format: Format, id: String) extends SizeWrap {
    def width = format.size.width

    def height = format.size.height
  }

  case class Stream(format: Format, image: InputStream)

  def resize(file: java.io.File, formats: Map[String, Format], store: Stream => Future[String])(implicit ec: ExecutionContext): Future[Map[String, File]] = {
    // get file image size
    read(file).flatMap {
      case Some(buf) =>
        val originalSize = Size(buf.getWidth, buf.getHeight)
        val (bigger, smaller) = formats.toSeq.partition(_._2 > originalSize)

        // for bigger formats -- store file as is only once
        val biggerSizes = if (bigger.nonEmpty) {
          val f = Format(originalSize, 0.9)
          val im = format(buf, f)
          store(im).map {
            fileId =>
              val oIm = File(f, fileId)
              bigger.map(bf => (bf._1, oIm))
          }
        } else Future successful Seq[(String, File)]()

        // for smaller formats -- resize
        val smallerSizes = if (smaller.nonEmpty) {
          Future sequence (for {s <- smaller} yield {
            val f = s._2.shape(originalSize)
            store(format(buf, f)).map(fileId => (s._1, File(f, fileId)))
          })
        } else Future successful Seq[(String, File)]()

        // collect all formats to a map
        for {
          bs <- biggerSizes
          ss <- smallerSizes
        } yield (bs ++ ss).toMap
      case None =>
        Future failed Unwished.UnprocessableEntity
    }
  }

  private def format(source: BufferedImage, format: Format) = {
    val out = new ByteArrayOutputStream()

    Thumbnails
      .of(source)
      .size(format.width, format.height)
      .outputFormat("jpg")
      .outputQuality(format.quality)
      .useExifOrientation(true)
      .toOutputStream(out)

    Stream(format, new ByteArrayInputStream(out.toByteArray))
  }

  private def read(original: java.io.File)(implicit ec: ExecutionContext) = future {
    try {
      val buf = ImageIO.read(original)
      if (buf == null) None
      else Some(buf)
    } catch {
      case e: IOException =>
        None
    }
  }
}
