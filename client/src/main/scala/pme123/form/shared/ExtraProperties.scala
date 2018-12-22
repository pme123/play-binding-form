package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.ElementType.{CHECKBOX, DIVIDER, DROPDOWN, TEXTFIELD, TITLE}
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.ExtraPropValue.ExtraValue

import scala.collection.immutable.IndexedSeq

case class ExtraProperties(propValues: Seq[ExtraPropValue] = Nil) {

  val hasExtras: Boolean = propValues.nonEmpty

  def valueFor(extraProp: ExtraProp): Option[String] =
    propValues.find(_.extraProp == extraProp)
      .flatMap(_.value)
}

object ExtraProperties {

  def apply(elementType: ElementType): ExtraProperties = {
    elementType match {
      case TEXTFIELD =>
        TextElem.extras
      case DROPDOWN =>
        DropdownElem.extras
      case CHECKBOX =>
        CheckboxElem.extras
      case TITLE =>
        TitleElem.extras
      case DIVIDER =>
        DividerElem.extras
      case _ => ExtraProperties()
    }
  }

  object TextElem {
    def extras: ExtraProperties = {

      ExtraProperties()
    }
  }

  object DropdownElem {
    def extras: ExtraProperties = {

      ExtraProperties(Seq(
        ExtraPropValue(
          CLEARABLE, Some("true")
        ))
      )
    }
  }

  object CheckboxElem {
    def extras: ExtraProperties = {

      ExtraProperties(Seq(
        ExtraPropValue(
          CHECKBOX_TYPE, CheckboxType.defaultValue
        ))
      )
    }

    sealed trait CheckboxType
      extends EnumEntry
        with I18nEnum {

      def i18nKey = s"enum.checkbox-type.${entryName.toLowerCase}"

    }


    object CheckboxType
      extends Enum[CheckboxType]
        with PlayInsensitiveJsonEnum[CheckboxType] {

      def defaultValue: Option[String] = Some(STANDARD.entryName)

      val values: IndexedSeq[CheckboxType] = findValues

      // Used for: TitleElem
      case object STANDARD extends CheckboxType

      case object SLIDER extends CheckboxType

      case object TOGGLE extends CheckboxType

    }

  }


  object TitleElem {

    sealed trait SizeType
      extends EnumEntry
        with I18nEnum {

      def cssClass: String = entryName.toLowerCase

      def i18nKey: String = s"enum.size-type.${entryName.toLowerCase}"

    }


    object SizeType
      extends Enum[SizeType]
        with PlayInsensitiveJsonEnum[SizeType] {

      def defaultValue: Option[String] = Some(MEDIUM.entryName)

      val values: IndexedSeq[SizeType] = findValues

      // Used for: TitleElem
      case object HUGE extends SizeType

      case object BIG extends SizeType

      case object MEDIUM extends SizeType

      case object SMALL extends SizeType

      case object TINY extends SizeType

    }

    def sizeElem = ExtraPropValue(SIZE, SizeType.defaultValue)

    def extras: ExtraProperties = {
      ExtraProperties(
        Seq(sizeElem)
      )
    }
  }

  object DividerElem {
    def extras: ExtraProperties = {

      ExtraProperties(
        Seq(TitleElem.sizeElem)
      )
    }
  }

  implicit val jsonFormat: OFormat[ExtraProperties] = Json.format[ExtraProperties]


}


case class ExtraPropValue(extraProp: ExtraProp, value: ExtraValue)

object ExtraPropValue {
  type ExtraValue = Option[String]

  implicit val jsonFormat: OFormat[ExtraPropValue] = Json.format[ExtraPropValue]

}


