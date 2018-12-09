package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed trait HeaderType
  extends EnumEntry {
  def styleName: String
}

object HeaderType
  extends Enum[HeaderType]
    with PlayInsensitiveJsonEnum[HeaderType] {

  val values: IndexedSeq[HeaderType] = findValues

  case object HUGE extends HeaderType {
    val styleName = "huge"
  }

  case object LARGE extends HeaderType {
    val styleName = "large"
  }

  case object MEDIUM extends HeaderType {
    val styleName = "medium"
  }

  case object SMALL extends HeaderType {
    val styleName = "small"
  }
  case object TINY extends HeaderType {
    val styleName = "tiny"
  }


}
