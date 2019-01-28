package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.HttpMethod.GET

import scala.collection.immutable.IndexedSeq
import scala.util.Random

case class MockContainer(ident: String,
                         mocks: Seq[MockEntry] = Nil)

object MockContainer {

  def defaultIdent = s"mock-${Random.nextInt(1000)}"

  implicit val jsonFormat: OFormat[MockContainer] = Json.format[MockContainer]
}

case class MockEntry(id: Int = Random.nextInt(1000), url: String = "", status: Int = 200, content: String = "") {

}

object MockEntry {

  implicit val jsonFormat: OFormat[MockEntry] = Json.format[MockEntry]
}

case class ServiceRequest(serviceConf: String,
                          path: String = "",
                          httpMethod: HttpMethod = GET,
                          payload: Option[String] = None) {

}

object ServiceRequest {

  implicit val jsonFormat: OFormat[ServiceRequest] = Json.format[ServiceRequest]
}

sealed trait HttpMethod
  extends EnumEntry {
}

// see https://github.com/lloydmeta/enumeratum#usage
object HttpMethod
  extends Enum[HttpMethod]
    with PlayInsensitiveJsonEnum[HttpMethod] {

  val values: IndexedSeq[HttpMethod] = findValues

  case object GET extends HttpMethod

  case object POST extends HttpMethod

  case object HEAD extends HttpMethod

  case object PATCH extends HttpMethod

  case object PUT extends HttpMethod

  case object DELETE extends HttpMethod

}