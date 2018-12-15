package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed trait ExtraProp
  extends EnumEntry


object ExtraProp
  extends Enum[ExtraProp]
    with PlayInsensitiveJsonEnum[ExtraProp] {

  val values: IndexedSeq[ExtraProp] = findValues

  // Used for: TitleElem / DividerElem
  case object SIZE extends ExtraProp
  // Used for: Dropdown
  case object CLEARABLE extends ExtraProp
  // Used for: Checkbox
  case object CHECKBOX_TYPE extends ExtraProp

}

