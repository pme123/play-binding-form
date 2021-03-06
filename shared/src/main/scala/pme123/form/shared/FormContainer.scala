package pme123.form.shared

import play.api.libs.json.{Json, OFormat}

case class FormContainer(ident: String, elems: Seq[BaseElement] = Nil)

object FormContainer {

  implicit val jsonFormat: OFormat[FormContainer] = Json.format[FormContainer]
}