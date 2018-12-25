package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import play.api.libs.json.{JsString, JsValue, Json, OFormat}
import pme123.form.shared.StructureType.STRING

import scala.collection.immutable
import scala.util.Random


case class DataStructure(ident: String, structureType: StructureType, structure: JsValue)

object DataStructure {

  def apply(): DataStructure =
    DataStructure(s"ds-${Random.nextInt(10000)}", STRING, JsString(""))

  implicit val jsonFormat: OFormat[DataStructure] = Json.format[DataStructure]
}

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