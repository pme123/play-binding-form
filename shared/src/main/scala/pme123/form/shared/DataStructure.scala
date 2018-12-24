package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.collection.immutable


case class DataStructure(ident: String, structureType: StructureType, structure: JsValue)

object DataStructure {

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