package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.PropTabType.findValues

import scala.collection.immutable.IndexedSeq


sealed trait TextType
  extends EnumEntry {
  def label: String
}


object TextType
  extends Enum[TextType]
    with PlayInsensitiveJsonEnum[TextType] {

  val values: IndexedSeq[TextType] = findValues

  case object LABEL extends TextType {
    val label = "Label"
  }

  case object PLACEHOLDER extends TextType {
    val label = "Placeholder"
  }

  case object TOOLTIP extends TextType {
    val label = "Tooltip"
  }

}










