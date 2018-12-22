package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN}

import scala.collection.immutable.IndexedSeq

object DataTypes {

  def defaultDataType(elementType: ElementType): DataType = {
    elementType match {
      case DROPDOWN =>
        DataType.ENUM
      case CHECKBOX =>
        DataType.BOOLEAN
      case _ => DataType.STRING
    }
  }

}

sealed trait DataType
  extends EnumEntry {


  def defaultValue: Option[String] = Some("")

  def i18nKey = s"enum.data-type.${entryName.toLowerCase}"

}


object DataType
  extends Enum[DataType]
    with PlayInsensitiveJsonEnum[DataType] {

  val values: IndexedSeq[DataType] = findValues

  case object STRING extends DataType

  case object LONG extends DataType
  case object DOUBLE extends DataType
  case object DATE extends DataType
  case object BOOLEAN extends DataType
  case object ENUM extends DataType
}

