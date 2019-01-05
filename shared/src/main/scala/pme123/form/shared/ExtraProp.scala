package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed trait ExtraProp
  extends EnumEntry {

  def defaultValue: String
}


object ExtraProp
  extends Enum[ExtraProp]
    with PlayInsensitiveJsonEnum[ExtraProp] {

  val values: IndexedSeq[ExtraProp] = findValues

  // Used for: TitleElem / DividerElem
  case object SIZE_CLASS extends ExtraProp {
    def defaultValue: String = SizeClass.defaultValue
  }

  // Used internal for Textfield
  case object SIZE extends ExtraProp {
    def defaultValue: String = "20"
  }

  // Used for: Dropdown
  case object CLEARABLE extends ExtraProp {
    def defaultValue: String = "false"
  }

  // Used for: Checkbox
  case object CHECKBOX_TYPE extends ExtraProp {
    def defaultValue: String = CheckboxType.defaultValue
  }

  // Used for: Textfield
  case object INPUT_TYPE extends ExtraProp {
    def defaultValue: String = InputType.defaultValue
  }

}

sealed trait SizeClass
  extends EnumEntry
    with I18nEnum {

  def cssClass: String = entryName.toLowerCase

  def i18nKey: String = s"enum.size-type.${entryName.toLowerCase}"

}


object SizeClass
  extends Enum[SizeClass]
    with PlayInsensitiveJsonEnum[SizeClass] {

  def defaultValue: String = BIG.key

  val values: IndexedSeq[SizeClass] = findValues

  // Used for: TitleElem
  case object HUGE extends SizeClass

  case object BIG extends SizeClass

  case object SMALL extends SizeClass

  case object TINY extends SizeClass

}


sealed trait CheckboxType
  extends EnumEntry
    with I18nEnum {

  def i18nKey = s"enum.checkbox-type.${entryName.toLowerCase}"

}


object CheckboxType
  extends Enum[CheckboxType]
    with PlayInsensitiveJsonEnum[CheckboxType] {

  def defaultValue: String = STANDARD.key

  val values: IndexedSeq[CheckboxType] = findValues

  // Used for: TitleElem
  case object STANDARD extends CheckboxType

  case object SLIDER extends CheckboxType

  case object TOGGLE extends CheckboxType

}


sealed trait InputType
  extends EnumEntry
    with I18nEnum {

  def i18nKey: String = key
}


object InputType
  extends Enum[InputType]
    with PlayInsensitiveJsonEnum[InputType] {

  def defaultValue: String = TEXT.key

  val values: IndexedSeq[InputType] = findValues

  // Used for: TitleElem

  case object DATE extends InputType

  case object HIDDEN extends InputType

  case object MONTH extends InputType

  case object NUMBER extends InputType

  case object PASSWORD extends InputType

  case object TEXT extends InputType

  case object TIME extends InputType

  case object WEEK extends InputType

}


