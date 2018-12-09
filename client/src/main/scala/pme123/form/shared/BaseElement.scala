package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.SIZE
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
                       value: String = "",
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: ElementEntries = ElementEntries()
                      ) {

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
    )
  }

  def extras(elementType: ElementType, supportedLangs: Seq[Language]): Map[ExtraProp, BaseElement] = {
    elementType match {
      case TITLE =>
        TitleElem.extras(supportedLangs)
      case _ => Map.empty
    }
  }
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
        value = "huge",
      ))
  }
}

case class ElementEntries(entries: Seq[ElementEntry] = Nil)

case class ElementEntry(ident: String, label: ElementText)


