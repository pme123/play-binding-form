package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

import scala.collection.immutable.IndexedSeq

sealed trait LayoutWide
  extends EnumEntry {

  def labels: Map[Language, String]

}


object LayoutWide
  extends Enum[LayoutWide]
    with PlayInsensitiveJsonEnum[LayoutWide] {

  val values: IndexedSeq[LayoutWide] = findValues

  case object FOUR extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "vier",
        EN -> "four"
      )
  }

  case object FIVE extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "fünf",
        EN -> "five"
      )
  }

  case object SIX extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "sechs",
        EN -> "six"
      )
  }

  case object SEVEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "sieben",
        EN -> "seven"
      )
  }

  case object EIGHT extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "acht",
        EN -> "eight"
      )
  }

  case object NINE extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "neun",
        EN -> "nine"
      )
  }

  case object TEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "zehn",
        EN -> "ten"
      )
  }

  case object ELEVEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "elf",
        EN -> "eleven"
      )
  }

  case object TWELVE extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "zwölf",
        EN -> "twelve"
      )
  }

  case object THIRTEEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "dreizehn",
        EN -> "thirteen"
      )
  }

  case object FOURTEEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "vierzehn",
        EN -> "fourteen"
      )
  }

  case object FIFTEEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "fünfzehn",
        EN -> "fifteen"
      )
  }

  case object SIXTEEN extends LayoutWide{
    def labels: Map[Language, String] =
      Map(
        DE -> "sechzehn",
        EN -> "sixteen"
      )
  }


}
