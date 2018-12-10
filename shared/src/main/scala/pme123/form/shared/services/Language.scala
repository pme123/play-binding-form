package pme123.form.shared.services

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed trait Language extends EnumEntry {

  def label: String

  def abbreviation: String

  def flag: String

  def i18nKey = s"enum.language.$abbreviation"

  def labelUppercase: String = label.toUpperCase

  def labelLowercase: String = label.toLowerCase

  def abbreviationUppercase: String = abbreviation.toUpperCase
}


// see https://github.com/lloydmeta/enumeratum#usage
object Language
  extends Enum[Language]
    with PlayInsensitiveJsonEnum[Language]{

  val values: IndexedSeq[Language] = findValues

  case object DE extends Language {
    override def label: String = "Deutsch"

    override def abbreviation: String = "de"

    val flag = "de"
  }

  case object EN extends Language {
    override def label: String = "English"

    override def abbreviation: String = "en"
    val flag = "gb"

  }

  case object FR extends Language {
    override def label: String = "Française"

    override def abbreviation: String = "fr"

    val flag = "fr"

  }

  case object IT extends Language {
    override def label: String = "Italiano"

    override def abbreviation: String = "it"

    val flag = "it"

  }

}
