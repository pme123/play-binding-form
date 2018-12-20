package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq
import com.softwaremill.quicklens._

case class Validations(rules: Seq[ValidationRule])

case class ValidationRule(validationType: ValidationType, enabled: Boolean = false, params: ValidationParams = ValidationParams()) {

  val semanticType: String = validationType.entryName.toLowerCase + params.semanticParams

}

object ValidationRule {

  implicit class ValidationRuleOps(vRule: ValidationRule) {
    def changeParam(param: String, value: String): ValidationParams = param match {
      case "p" => vRule.params.modify(_.stringParam)
        .setTo(Some(value))
      case "p1" => vRule.params.modify(_.intParam1)
        .setTo(Some(value.toInt))
      case "p2" => vRule.params.modify(_.intParam2)
        .setTo(Some(value.toInt))
    }
  }

}

case class ValidationParams(stringParam: Option[String] = None,
                            intParam1: Option[Int] = None,
                            intParam2: Option[Int] = None) {

  val values: List[String] = List(stringParam, intParam1, intParam2).flatten.map(_.toString)

  val semanticParams: String = values match {
    case x :: y :: _ => s"[$x..$y]"
    case x :: _ => s"[$x]"
    case _ => ""
  }

}

sealed trait ValidationType
  extends EnumEntry
    with I18nEnum {

  def i18nKey: String = s"enum.validation-type.${entryName.toLowerCase}"

  def i18nKey(param: String): String = s"enum.validation-type.${entryName.toLowerCase}.$param"

  def promptI18nKey: String = s"$i18nKey.prompt"

}


object ValidationType
  extends Enum[ValidationType]
    with PlayInsensitiveJsonEnum[ValidationType] {

  val values: IndexedSeq[ValidationType] = findValues

  // case object EMPTY extends ValidationType
  case object EMAIL extends ValidationType

  case object INTEGER extends ValidationType

  case object REG_EXP extends ValidationType


}
