package pme123.form.shared

import com.softwaremill.quicklens._
import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared.ValidationType.{EMAIL, INTEGER, REG_EXP}

import scala.collection.immutable.IndexedSeq

case class Validations(rules: Seq[ValidationRule] = Nil)

object Validations {

  def apply(elementType: ElementType): Validations = {
    elementType match {
      case TEXTFIELD =>
        Validations(Seq(ValidationRule(EMAIL), ValidationRule(INTEGER,
          params = ValidationParams(intParam1 = Some(0), intParam2 = Some(100))),
          ValidationRule(REG_EXP,
            params = ValidationParams(stringParam = Some("")))
        ))
      case _ => Validations()
    }
  }

  implicit val jsonFormat: OFormat[Validations] = Json.format[Validations]

}

case class ValidationRule(validationType: ValidationType, enabled: Boolean = false, params: ValidationParams = ValidationParams())

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

    lazy val semanticType: String = vRule.validationType.entryName.toLowerCase + semanticParams

    lazy val semanticParams: String = vRule.params.values match {
      case x :: y :: _ => s"[$x..$y]"
      case x :: _ => s"[$x]"
      case _ => ""
    }
  }

  implicit val jsonFormat: OFormat[ValidationRule] = Json.format[ValidationRule]

}

case class ValidationParams(stringParam: Option[String] = None,
                            intParam1: Option[Int] = None,
                            intParam2: Option[Int] = None) {

  val values: List[String] = List(stringParam, intParam1, intParam2).flatten.map(_.toString)


}

object ValidationParams {
  implicit val jsonFormat: OFormat[ValidationParams] = Json.format[ValidationParams]
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
