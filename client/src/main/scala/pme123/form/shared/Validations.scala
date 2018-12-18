package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

case class Validations(rules: Seq[ValidationRule])

case class ValidationRule(validationType: ValidationType, enabled: Boolean = false, params: List[String] = Nil) {


  val semanticParams: String =  params match {
    case x::y::_ =>  s"[$x..$y]"
    case x::_ =>  s"[$x]"
    case _ => ""
  }

  val semanticType: String = validationType.entryName.toLowerCase + semanticParams


}


sealed trait ValidationType
  extends EnumEntry
    with I18nEnum {

  def i18nKey: String = s"enum.validation-type.${entryName.toLowerCase}"
  def promptI18nKey: String = s"$i18nKey.prompt"

}


object ValidationType
  extends Enum[ValidationType]
    with PlayInsensitiveJsonEnum[ValidationType] {

  val values: IndexedSeq[ValidationType] = findValues

 // case object EMPTY extends ValidationType
  case object EMAIL extends ValidationType
  case object INTEGER extends ValidationType


}
