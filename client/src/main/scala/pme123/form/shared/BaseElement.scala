package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.LayoutWide.EIGHT
import pme123.form.shared.TextType.{LABEL, TOOLTIP}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq
import scala.util.Random

case class BaseElement(ident: String,
                       elementType: ElementType,
                       texts: Option[ElementTexts],
                       extras: Map[ExtraProp, BaseElement] = Map.empty,
                       value: Option[String] = Some(""),
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: Option[ElementEntries] = None
                      ) {

  val hasTexts: Boolean = texts.nonEmpty
  val hasExtras: Boolean = extras.nonEmpty
  val hasEntries: Boolean = elemEntries.nonEmpty

}

object BaseElement {
  def ident(elementType: ElementType) = s"${elementType.entryName}-${Random.nextInt(100000)}"

  def apply(elementType: ElementType)
           (implicit supportedLangs: Seq[Language]): BaseElement = {

    def texts = elementType match {
      case SPACER => None
      case _ => Some(ElementTexts(ident(elementType)))
    }

    BaseElement(
      ident(elementType),
      elementType,
      texts,
      extras(elementType),
      elementType.defaultValue,
      elemEntries = entries(elementType)
    )
  }

  def extras(elementType: ElementType)(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {
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

  def entries(elementType: ElementType): Option[ElementEntries] = {
    elementType match {
      case DROPDOWN =>
        Some(ElementEntries())
      case _ => None
    }
  }

  def enumEntries(enums: Seq[I18nEnum])(implicit supportedLangs: Seq[Language]): Some[ElementEntries] = {
    Some(ElementEntries(
      enums.map(enum => ElementEntry(enum.entryName, ElementText.label(enum.i18nKey)))
    ))
  }
}

case class ElementEntries(entries: Seq[ElementEntry] = Nil)

case class ElementEntry(ident: String, label: ElementText)

object ElementEntry {
  def apply()(implicit supportedLangs: Seq[Language]): ElementEntry = new
      ElementEntry("", ElementText.emptyLabel)
}

object DropdownElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      CLEARABLE -> BaseElement(CLEARABLE.entryName,
        CHECKBOX,
        Some(ElementTexts(
          ElementText(LABEL, Map(DE -> "Wert löschen", EN -> "Clearable")),
          ElementText.emptyPlaceholder,
          ElementText(TOOLTIP, Map(DE -> "Auswählen wenn kein Wert möglich ist ", EN -> "Check this if no value should be possible"))
        )),
        value = Some("true"),
      ))
  }
}

object CheckboxElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      CHECKBOX_TYPE -> BaseElement(CHECKBOX_TYPE.entryName,
        DROPDOWN,
        Some(ElementTexts(
          ElementText(LABEL, Map(DE -> "Art", EN -> "Type")),
          ElementText.emptyPlaceholder,
          ElementText(TOOLTIP, Map(DE -> "Art der Check-Box", EN -> "Type of the Checkbox"))
        )),
        elemEntries = BaseElement.enumEntries(CheckboxType.values),
        value = CheckboxType.defaultValue,
      ))
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

  def sizeElem(implicit supportedLangs: Seq[Language]) = BaseElement(SIZE.entryName,
    DROPDOWN,
    Some(ElementTexts(
      ElementText(LABEL, Map(DE -> "Grösse", EN -> "Size")),
      ElementText.emptyPlaceholder,
      ElementText(TOOLTIP, Map(DE -> "Grösse des Titels (huge, big, medium, small, tiny)", EN -> "Size of the title (huge, big, medium, small, tiny)"))
    )),
    value = SizeType.defaultValue,
    elemEntries = BaseElement.enumEntries(SizeType.values),
  )

  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {
    Map(
      SIZE -> sizeElem
    )
  }
}

object DividerElem {
  def extras(implicit supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

    Map(
      SIZE -> TitleElem.sizeElem
    )
  }
}


