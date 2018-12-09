package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq


sealed trait ElementType
  extends EnumEntry


object ElementType
  extends Enum[ElementType]
    with PlayInsensitiveJsonEnum[ElementType] {

  val values: IndexedSeq[ElementType] = findValues

  case object TEXTFIELD extends ElementType

  case object TEXTAREA extends ElementType

  case object TITLE extends ElementType

  case object CHECKBOX extends ElementType

  case object DROPDOWN extends ElementType

}



