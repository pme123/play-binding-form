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
                       defaultValue: String = "",
                       layoutWide: LayoutWide = EIGHT,
                       changeEvent: Option[(ExtraProp, String) => Unit] = None,
                      ) {

}

object BaseElement {
  def apply(elementType: ElementType,
            supportedLangs: Seq[Language],
            changeEvent: Option[(ExtraProp, String) => Unit]): BaseElement = {
    val ident = s"${elementType.entryName}-${Random.nextInt(100000)}"

    BaseElement(
      ident,
      elementType,
      ElementTexts(supportedLangs, ident),
      extras(elementType, supportedLangs, changeEvent),
    )
  }

  def extras(elementType: ElementType, supportedLangs: Seq[Language], changeEvent: Option[(ExtraProp, String) => Unit]): Map[ExtraProp, BaseElement] = {
    elementType match {
      case TITLE =>
        TitleElem.extras(supportedLangs, changeEvent)
      case _ => Map.empty
    }
  }
}

object TitleElem {
  def extras(supportedLangs: Seq[Language], changeEvent: Option[(ExtraProp, String) => Unit]): Map[ExtraProp, BaseElement] = {
    Map(
      SIZE -> BaseElement(SIZE.entryName,
        TEXTFIELD,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Grösse", EN -> "Size")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Grösse des Titels (huge, big, medium, small, tiny)", EN -> "Size of the title (huge, big, medium, small, tiny)"))
        ),
        defaultValue = "huge",
        changeEvent = changeEvent
      ))
  }
}

sealed trait LayoutWide
  extends EnumEntry


object LayoutWide
  extends Enum[LayoutWide]
    with PlayInsensitiveJsonEnum[LayoutWide] {

  val values: IndexedSeq[LayoutWide] = findValues

  case object ONE extends LayoutWide

  case object TWO extends LayoutWide

  case object THREE extends LayoutWide

  case object FOUR extends LayoutWide

  case object FIVE extends LayoutWide

  case object SIX extends LayoutWide

  case object SEVEN extends LayoutWide

  case object EIGHT extends LayoutWide

  case object NINE extends LayoutWide

  case object TEN extends LayoutWide

  case object ELEVEN extends LayoutWide

  case object TWELVE extends LayoutWide

  case object THIRTEEN extends LayoutWide

  case object FOURTEEN extends LayoutWide

  case object FIFTEEN extends LayoutWide

  case object SIXTEEN extends LayoutWide


}