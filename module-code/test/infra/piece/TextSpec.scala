package infra.piece

import play.api.test.{FakeApplication, WithApplication, PlaySpecification}
import play.api.libs.json.Json
import scala.concurrent.duration.FiniteDuration
import infra.piece.core.Piece
import infra.piece.text.TextPiece

/**
 * @author alari (name.alari@gmail.com)
 * @since 09.05.14 17:46
 */
class TextSpec extends PlaySpecification {
  "Text Json" should {
    val appl = FakeApplication(additionalPlugins = Seq("infra.piece.text.TextKind"))

    "Be converted to TextPiece" in new WithApplication(appl) {
      val piece = Piece.reads.reads(Json.obj(
        "kind" -> "text",
        "engine" -> "markdown",
        "source" -> "test"
      )).asEither
      piece.isRight must beTrue
      piece.right.get must beAnInstanceOf[TextPiece]

      //Piece.handle(piece.right.get) must beSome[Piece](TextPiece(id = piece.right.get.id, source = Some("test"), title = None, processed = Some("<p>test</p>"))).await(1, FiniteDuration(1, "second"))

    }

    "return nothing when text is empty" in new WithApplication(appl) {
      val piece = Piece.reads.reads(Json.obj(
        "kind" -> "text",
        "engine" -> "markdown",
        "source" -> "\n \n\t \n"
      )).asEither
      piece.isRight must beTrue
      piece.right.get must beAnInstanceOf[TextPiece]

      //Piece.handle(piece.right.get) must beNone.await(1, FiniteDuration(1, "second"))

    }
  }
}
