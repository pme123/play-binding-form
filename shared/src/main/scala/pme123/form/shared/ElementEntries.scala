package pme123.form.shared

import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.services.Language

import scala.util.Random

case class ElementEntries(entries: Seq[ElementEntry] = Nil)

object ElementEntries {

  implicit val jsonFormat: OFormat[ElementEntries] = Json.format[ElementEntries]

}

case class ElementEntry(key: String, label: ElementText, ident: String = ElementEntry.ident) {

}

object ElementEntry {

  def ident = s"ENTRY-${Random.nextInt(10000)}"

  def apply()(implicit supportedLangs: Seq[Language]): ElementEntry =
    ElementEntry("", ElementText.emptyLabel)

  def apply(key: String)(implicit supportedLangs: Seq[Language]): ElementEntry =
    ElementEntry(key, ElementText.simple(key))

  implicit val jsonFormat: OFormat[ElementEntry] = Json.format[ElementEntry]

}
