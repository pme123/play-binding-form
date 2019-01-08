package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import julienrf.json.derived
import play.api.libs.json.OFormat

import scala.collection.immutable
import scala.util.Random

case class DataContainer(ident: String= DataStructure.defaultKey, structure: DataObject = DataObject())

object DataContainer {
  implicit val jsonFormat: OFormat[DataContainer] = derived.oformat[DataContainer]()

}

sealed abstract class DataStructure {
}

object DataStructure {

  import StructureType._

  def defaultKey = s"data-${Random.nextInt(1000)}"

  def apply(structureType: StructureType): DataStructure = structureType match {
    case STRING => DataString()
    case BOOLEAN => DataBoolean()
    case NUMBER => DataNumber()
    case OBJECT => DataObject()
  }

  implicit val jsonFormat: OFormat[DataStructure] = derived.oformat[DataStructure]()

}

case class DataObject(value: Seq[(String, DataStructure)] = Seq.empty)
  extends DataStructure

object DataObject {

  implicit val jsonFormat: OFormat[DataObject] = derived.oformat[DataObject]()
}

case class DataString(value: String = "")
  extends DataStructure

case class DataNumber(value: BigDecimal = 0)
  extends DataStructure

case class DataBoolean(value: Boolean = false)
  extends DataStructure


sealed trait StructureType
  extends EnumEntry {

  def i18nKey = s"enum.structure-type.${entryName.toLowerCase}"

}


object StructureType
  extends Enum[StructureType]
    with PlayInsensitiveJsonEnum[StructureType] {

  def values: immutable.IndexedSeq[StructureType] = findValues

  case object STRING extends StructureType

  case object NUMBER extends StructureType

  case object BOOLEAN extends StructureType

  case object OBJECT extends StructureType

}