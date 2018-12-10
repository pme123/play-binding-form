package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq


sealed trait ElementType
  extends EnumEntry {


  def defaultValue: Option[String] = Some("")

  def i18nKey = s"enum.element-type.${entryName.toLowerCase}"

}


object ElementType
  extends Enum[ElementType]
    with PlayInsensitiveJsonEnum[ElementType] {

  val values: IndexedSeq[ElementType] = findValues

  case object TEXTFIELD extends ElementType

  case object TEXTAREA extends ElementType

  case object TITLE extends ElementType {

    override def defaultValue: Option[String] = None

  }

  case object CHECKBOX extends ElementType

  case object DROPDOWN extends ElementType

}



