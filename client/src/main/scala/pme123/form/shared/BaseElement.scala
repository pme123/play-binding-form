package pme123.form.shared

import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CLEARABLE, SIZE}
import pme123.form.shared.LayoutWide.EIGHT
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

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

  def apply(elementType: ElementType,
            supportedLangs: Seq[Language]): BaseElement = {
    val ident = s"${elementType.entryName}-${Random.nextInt(100000)}"

    BaseElement(
      ident,
      elementType,
      ElementTexts(supportedLangs, ident),
      extras(elementType, supportedLangs),
      elementType.defaultValue,
      elemEntries = entries(elementType)
    )
  }

  def extras(elementType: ElementType, supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {
    elementType match {
      case TITLE =>
        TitleElem.extras(supportedLangs)
      case DROPDOWN =>
       DropdownElem.extras(supportedLangs)
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
  def extras(supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

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
  def extras(supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {

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


