package pme123.form.shared

import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.SemanticField.FieldIdent


case class SemanticForm(on: String = "blur", inline : Boolean = true, fields: Map[FieldIdent, SemanticField] = Map.empty)

object SemanticForm {

  implicit val jsonFormat: OFormat[SemanticForm] = Json.format[SemanticForm]

}

case class SemanticField(identifier: FieldIdent, optional: Boolean, rules: Seq[SemanticRule])

object SemanticField {
  type FieldIdent = String
  implicit val jsonFormat: OFormat[SemanticField] = Json.format[SemanticField]

}

case class SemanticRule(`type`:  String, prompt: String)

object SemanticRule {

  implicit val jsonFormat: OFormat[SemanticRule] = Json.format[SemanticRule]


}

