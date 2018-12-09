package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq


sealed trait ElementType
  extends EnumEntry {


  def labels: Map[Language, String]

}


object ElementType
  extends Enum[ElementType]
    with PlayInsensitiveJsonEnum[ElementType] {

  val values: IndexedSeq[ElementType] = findValues

  case object TEXTFIELD extends ElementType {
    override def labels: Map[Language, String] =
      Map(
        DE -> "Text Feld",
        EN -> "Text Field"
      )
  }

  case object TEXTAREA extends ElementType {
    override def labels: Map[Language, String] =
      Map(
        DE -> "Text Bereich",
        EN -> "Text Area"
      )
  }

  case object TITLE extends ElementType {
    override def labels: Map[Language, String] =
      Map(
        DE -> "Titel",
        EN -> "Title"
      )
  }

  case object CHECKBOX extends ElementType {
    override def labels: Map[Language, String] =
      Map(
        DE -> "Check Box",
        EN -> "Checkbox"
      )
  }

  case object DROPDOWN extends ElementType {
    override def labels: Map[Language, String] =
      Map(
        DE -> "Auswahl",
        EN -> "Selection"
      )
  }

}



