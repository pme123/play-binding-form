package pme123.form.client

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.client.services.I18n
import pme123.form.shared.ElementType.{CHECKBOX, DIVIDER, DROPDOWN, TITLE}
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.TextType.{LABEL, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq

object BaseElementExtras {

  def extras(elementType: ElementType)(implicit supportedLangs: Seq[Language]): Map[ExtraProp, UIFormElem] = {
    elementType match {
      case DROPDOWN =>
        DropdownElem.extras
      case CHECKBOX =>
        CheckboxElem.extras
      case TITLE =>
        TitleElem.extras
      case DIVIDER =>
        DividerElem.extras
      case _ => Map.empty
    }
  }

  object DropdownElem {
    def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, UIFormElem] = {

      Map(
        CLEARABLE -> UIFormElem(
          BaseElement(CLEARABLE.entryName,
            CHECKBOX,
            DataType.BOOLEAN,
            texts = ElementTexts(
              Some(ElementText.label(Map(DE -> "Wert löschen", EN -> "Clearable"))),
              None,
              Some(ElementText.tooltip(Map(DE -> "Auswählen wenn kein Wert möglich ist ", EN -> "Check this if no value should be possible")))
            ),
            value = Some("true"),
          )))
    }
  }

  object CheckboxElem {
    def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, UIFormElem] = {

      Map(
        CHECKBOX_TYPE -> UIFormElem(BaseElement(CHECKBOX_TYPE.entryName,
          DROPDOWN,
          DataType.STRING,
          texts = ElementTexts(
            Some(ElementText.label(Map(DE -> "Art", EN -> "Type"))),
            None,
            Some(ElementText(TOOLTIP, Map(DE -> "Art der Check-Box", EN -> "Type of the Checkbox")))
          ),
          elemEntries = I18n.enumEntries(CheckboxType.values),
          value = CheckboxType.defaultValue,
        )))
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

    def sizeElem(implicit supportedLangs: Seq[Language]) =
      UIFormElem(BaseElement(SIZE.entryName,
        DROPDOWN,
        DataType.STRING,
          texts = ElementTexts(
          Some(ElementText(LABEL, Map(DE -> "Grösse", EN -> "Size"))),
          None,
          Some(ElementText(TOOLTIP, Map(DE -> "Grösse des Titels (huge, big, medium, small, tiny)", EN -> "Size of the title (huge, big, medium, small, tiny)")))
        ),
           value = SizeType.defaultValue,
        elemEntries = I18n.enumEntries(SizeType.values),

      ))

    def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, UIFormElem] = {
      Map(
        SIZE -> sizeElem
      )
    }
  }

  object DividerElem {
    def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, UIFormElem] = {

      Map(
        SIZE -> TitleElem.sizeElem
      )
    }
  }

}
