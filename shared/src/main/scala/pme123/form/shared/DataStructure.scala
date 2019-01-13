package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import julienrf.json.derived
import play.api.libs.json.OFormat

import scala.collection.immutable
import scala.util.Random

sealed abstract class DataStructure {
  def ident: String
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

case class DataObject(ident: String = "", value: Seq[DataStructure] = Seq.empty)
  extends DataStructure

object DataObject {

  implicit val jsonFormat: OFormat[DataObject] = derived.oformat[DataObject]()
}

case class DataString(ident: String = "", value: Option[String] = None)
  extends DataStructure

case class DataNumber(ident: String = "", value: Option[BigDecimal] = None)
  extends DataStructure

case class DataBoolean(ident: String = "", value: Option[Boolean] = None)
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