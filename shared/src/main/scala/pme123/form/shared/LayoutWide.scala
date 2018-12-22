package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed trait LayoutWide
  extends EnumEntry {

  def i18nKey = s"enum.layout-wide.${entryName.toLowerCase}"

}


object LayoutWide
  extends Enum[LayoutWide]
    with PlayInsensitiveJsonEnum[LayoutWide] {

  val values: IndexedSeq[LayoutWide] = findValues

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
