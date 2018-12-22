package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq


sealed trait PropTabType
  extends EnumEntry {

  def i18nKey = s"enum.prop-tab-type.${entryName.toLowerCase}"
}

object PropTabType
  extends Enum[PropTabType]
    with PlayInsensitiveJsonEnum[PropTabType] {

  val values: IndexedSeq[PropTabType] = findValues

  case object PROPERTIES extends PropTabType

  case object TEXTS extends PropTabType

  case object ENTRIES extends PropTabType

  case object VALIDATIONS extends PropTabType

}







