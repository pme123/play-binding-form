package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.LayoutWide.EIGHT
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq
import scala.util.Random

case class BaseElement(ident: String,
                       elementType: ElementType,
                       texts: ElementTexts,
                       extras: Map[ExtraProp, BaseElement] = Map.empty,
                       value: Option[String] = Some(""),
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: Option[ElementEntries] = None
                      ) {

  val hasExtras: Boolean = extras.nonEmpty
  val hasEntries: Boolean = elemEntries.nonEmpty

}

object BaseElement {

  def apply(elementType: ElementType)
           (implicit supportedLangs: Seq[Language]): BaseElement = {
    val ident = s"${elementType.entryName}-${Random.nextInt(100000)}"

    BaseElement(
      ident,
      elementType,
      ElementTexts(supportedLangs, ident),
      extras(elementType),
      elementType.defaultValue,
      elemEntries = entries(elementType)
    )
  }

  def extras(elementType: ElementType)(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {
    elementType match {
      case TITLE =>
        TitleElem.extras
      case DROPDOWN =>
        DropdownElem.extras
      case CHECKBOX =>
        CheckboxElem.extras
      case _ => Map.empty
    }
  }

  def entries(elementType: ElementType): Option[ElementEntries] = {
    elementType match {
      case DROPDOWN =>
        Some(ElementEntries())
      case _ => None
    }
  }
}

case class ElementEntries(entries: Seq[ElementEntry] = Nil)

case class ElementEntry(ident: String, label: ElementText)

object ElementEntry {
  def apply(supportedLangs: Seq[Language]): ElementEntry = new
      ElementEntry("", ElementText.empty(supportedLangs))
}

object TitleElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      SIZE -> BaseElement(SIZE.entryName,
        TEXTFIELD,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Grösse", EN -> "Size")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Grösse des Titels (huge, big, medium, small, tiny)", EN -> "Size of the title (huge, big, medium, small, tiny)"))
        ),
        value = Some("huge"),
      ))
  }
}

object DropdownElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      CLEARABLE -> BaseElement(CLEARABLE.entryName,
        CHECKBOX,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Wert löschen", EN -> "Clearable")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Auswählen wenn kein Wert möglich ist ", EN -> "Check this if no value should be possible"))
        ),
        value = Some("true"),
      ))
  }
}

object CheckboxElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      CHECKBOX_TYPE -> BaseElement(CHECKBOX_TYPE.entryName,
        DROPDOWN,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Art", EN -> "Type")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Art der Check-Box", EN -> "Type of the Checkbox"))
        ),
        elemEntries = Some(ElementEntries(
          CheckboxType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(enum.i18nKey)))
        )),
        value = CheckboxType.defaultValue,
      ))
  }

  sealed trait CheckboxType
    extends EnumEntry {

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


