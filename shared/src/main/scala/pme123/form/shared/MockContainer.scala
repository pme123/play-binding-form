package pme123.form.shared

import play.api.libs.json.{Json, OFormat}

import scala.util.Random

case class MockContainer(ident: String,
                         mocks: Seq[MockEntry] = Nil)

object MockContainer {

  def defaultIdent = s"mock-${Random.nextInt(1000)}"

  implicit val jsonFormat: OFormat[MockContainer] = Json.format[MockContainer]
}

case class MockEntry(url: String, content: String) {

}

object MockEntry {

  implicit val jsonFormat: OFormat[MockEntry] = Json.format[MockEntry]
}