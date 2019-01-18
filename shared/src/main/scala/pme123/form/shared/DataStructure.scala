package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import julienrf.json.derived
import play.api.libs.json._
import pme123.form.shared.Cardinality.{ONE, ZERO_TO_MANY}

import scala.collection.immutable
import scala.util.Random

sealed abstract class DataStructure {
  def ident: String

  def cardinality: Cardinality
}

object DataStructure {

  import StructureType._

  def defaultKey = s"data-${Random.nextInt(1000)}"

  def apply(ident: String, structureType: StructureType): DataStructure = structureType match {
    case OBJECT => DataObject(ident)
    case _ => DataValue(ident, structureType)
  }

  def fromJson(key: String, jsValue: JsValue): DataStructure = jsValue match {
    case js: JsObject =>
      DataObject(key, ONE, js.fields.map(f => fromJson(f._1, f._2)))
    case js: JsArray =>
      js.value.headOption match {
        case  Some(js2: JsObject) => DataObject(key, ZERO_TO_MANY, js2.fields.map(f => fromJson(f._1, f._2)))
        case  Some(_: JsBoolean) => DataValue(key, BOOLEAN, ZERO_TO_MANY)
        case  Some(_: JsNumber) => DataValue(key, NUMBER, ZERO_TO_MANY)
        case  _ => DataValue(key, STRING, ZERO_TO_MANY)
      }
    case _: JsBoolean => DataValue(key, BOOLEAN)
    case _: JsNumber => DataValue(key, NUMBER)
    case _ => DataValue(key, STRING)
  }

  implicit val jsonFormat: OFormat[DataStructure] = derived.oformat[DataStructure]()

}

case class DataObject(ident: String = "", cardinality: Cardinality = ONE, children: Seq[DataStructure] = Seq.empty)
  extends DataStructure {

  def child(ident: String): Option[DataStructure] =
    children.find(_.ident == ident)

}

object DataObject {

  implicit val jsonFormat: OFormat[DataObject] = derived.oformat[DataObject]()
}

case class DataValue(ident: String = "", structureType: StructureType, cardinality: Cardinality = ONE)
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

sealed trait Cardinality
  extends EnumEntry {

  def label: String
}

object Cardinality
  extends Enum[Cardinality]
    with PlayInsensitiveJsonEnum[Cardinality] {

  def values: immutable.IndexedSeq[Cardinality] = findValues

  case object ZERO_TO_ONE extends Cardinality {
    def label: String = "0..1"
  }

  case object ONE extends Cardinality {
    def label: String = "1"
  }

  case object ZERO_TO_MANY extends Cardinality {
    def label: String = "*"
  }

  case object ONE_TO_MANY extends Cardinality {
    def label: String = "1..*"
  }

}
