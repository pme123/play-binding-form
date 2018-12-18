package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

case class Validations(rules: Seq[ValidationRule])

case class ValidationRule(validationType: ValidationType, enabled: Boolean = false) {

  val semanticType: String = validationType.entryName.toLowerCase

}


sealed trait ValidationType
  extends EnumEntry
    with I18nEnum {

  def i18nKey: String = s"enum.validation-type.${entryName.toLowerCase}"

}


object ValidationType
  extends Enum[ValidationType]
    with PlayInsensitiveJsonEnum[ValidationType] {

  val values: IndexedSeq[ValidationType] = findValues

  case object EMPTY extends ValidationType
  case object EMAIL extends ValidationType


}
