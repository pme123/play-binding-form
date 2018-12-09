package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq


sealed trait PropTabType
  extends EnumEntry {
  def label: String
}

object PropTabType
  extends Enum[PropTabType]
    with PlayInsensitiveJsonEnum[PropTabType] {

  val values: IndexedSeq[PropTabType] = findValues

  case object PROPERTIES extends PropTabType {
    val label = "Properties"
  }

  case object TEXTS extends PropTabType {
    val label = "Texts"
  }

}







