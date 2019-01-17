package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import julienrf.json.derived
import play.api.libs.json.{JsObject, JsString, JsValue, OFormat}

import scala.collection.immutable
import scala.util.Random

sealed abstract class DataStructure {
  def ident: String
}

object DataStructure {

  import StructureType._

  def defaultKey = s"data-${Random.nextInt(1000)}"

  def apply(ident: String, structureType: StructureType): DataStructure = structureType match {
    case OBJECT => DataObject(ident)
    case _ => DataValue(ident, structureType)
  }

  def fromJson(key: String, jsValue: JsValue): DataStructure = jsValue match {
    case js: JsObject => DataObject(key, js.fields.map(f => fromJson(f._1, f._2)))
    case js: JsString => DataValue(key, STRING)
    case _ => DataValue(key, STRING)
  }

  implicit val jsonFormat: OFormat[DataStructure] = derived.oformat[DataStructure]()

}

case class DataObject(ident: String = "", children: Seq[DataStructure] = Seq.empty)
  extends DataStructure

object DataObject {

  implicit val jsonFormat: OFormat[DataObject] = derived.oformat[DataObject]()
}

case class DataValue(ident: String = "", structureType: StructureType)
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