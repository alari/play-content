package infra.piece

import play.api.test.{WithApplication, PlaySpecification}
import java.net.URL
import infra.piece.youtube.{YoutubeKind, YoutubePiece}
import infra.piece.core.Piece
import scala.concurrent.ExecutionContext

/**
 * @author alari (name.alari@gmail.com)
 * @since 09.05.14 17:41
 */
class YoutubeSpec extends PlaySpecification {

  "YouTube piece" should {

    "Extract codes correctly" in new WithApplication {
      val kind = new YoutubeKind
      kind.extractCode(new URL("http://youtu.be/zi3AqicZgEk")) must beSome("zi3AqicZgEk")
      kind.extractCode(new URL("http://www.youtube.com/watch?v=zi3AqicZgEk&feature=g-logo&context=G2e33cabFOAAAAAAABAA")) must beSome("zi3AqicZgEk")
      kind.extractCode(new URL("http://www.youtube.com/look?v=zi3AqicZgEk&feature=g-logo&context=G2e33cabFOAAAAAAABAA")) must beNone
    }
    "Be correctly built from json" in new WithApplication {
      val kind = new YoutubeKind
      kind.handleUrl(ExecutionContext.global)(new URL("http://youtu.be/zi3AqicZgEk")) must beLike[Piece] {
        case yt: YoutubePiece =>
          yt.code must be_==("zi3AqicZgEk")
          yt.title must beSome("Новогоднее обращение президента 2011 (31.12.2011)")
          yt.description must beSome("Новогоднее обращение президента 2011-2012 (31.12.2011)\nтут полная версия http://www.youtube.com/watch?v=llLdGooDRs0")
          yt.thumbs must beSome
          yt.thumbs.get.find(t => t._1 == "default" && t._2.endsWith(".jpg")) must beSome
      }.await

    }
  }
}