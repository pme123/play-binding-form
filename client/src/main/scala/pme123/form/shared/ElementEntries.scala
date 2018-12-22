package pme123.form.shared

import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.ElementType.DROPDOWN
import pme123.form.shared.services.Language

import scala.util.Random

case class ElementEntries(hasEntries: Boolean = false, entries: Seq[ElementEntry] = Nil)

object ElementEntries {

  def apply(elementType: ElementType): ElementEntries = {
    elementType match {
      case DROPDOWN =>
        ElementEntries(hasEntries = true)
      case _ => ElementEntries()
    }
  }

  def apply(entries: Seq[ElementEntry]): ElementEntries = {
    ElementEntries(hasEntries = true, entries)
  }

  implicit val jsonFormat: OFormat[ElementEntries] = Json.format[ElementEntries]

}

case class ElementEntry(key: String, label: ElementText, ident: String = ElementEntry.ident) {

}

object ElementEntry {

  def ident = s"ENTRY-${Random.nextInt(10000)}"

  def apply()(implicit supportedLangs: Seq[Language]): ElementEntry =
    ElementEntry("", ElementText.emptyLabel)

  implicit val jsonFormat: OFormat[ElementEntry] = Json.format[ElementEntry]

}
