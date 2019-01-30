package pme123.form.shared

import java.io.{File, InputStream}

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import julienrf.json.derived
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.collection.immutable
import scala.io.Source

object JsonSchema {

  def fromInputStream(is: InputStream): SchemaRoot = {
    if (is == null) throw new Exception("Input stream is null")
    fromJson(Source.fromInputStream(is).getLines.mkString("\n"))
  }

  def fromURL(url: String): SchemaRoot = {
    fromJson(Source.fromURL(url).getLines.mkString("\n"))
  }

  def fromResource(path: String): SchemaRoot =
    {
      println(getClass.getClassLoader.getResource("pets.json"))
      fromInputStream(getClass.getResourceAsStream(path))
    }

  def fromJson(str: String): SchemaRoot = {
    val json = Json.parse(str)

    SchemaRoot(json)
  }

  /**
    * Convenience method for creating a schema from a simple type (e.g. { "type" : "string" }
    */
  def schemaFromSimpleType(st: SimpleType) = SchemaRoot(`type` = Some(SimpleJsType(st)))

  /**
    * Convenience method for creating schemas that represent an object (based on given fields)
    */
  def schemaFromFields(fields: List[Field]) = SchemaRoot(`type` = Some(SimpleJsType(SimpleType.OBJECT)), properties = Some(fields))

  /**
    * Convenience method for creating schemas that reference another schema
    */
  def schemaFromRef(ref: String) = SchemaRoot($ref = Some(ref))

  /**
    * Convenience method for creating schemas from enums
    *
    * @param enum A list if items to enum (encoded as json strings)
    */
  def schemaFromEnum(enum: List[String]) = SchemaRoot(enum = Some(enum))

  /**
    * Convenience method for creating schemas from an array
    *
    * @param schema A schema that defines the type of the array
    */
  def schemaFromArray(schema: SchemaRoot) = SchemaRoot(`type` = Some(SimpleJsType(SimpleType.ARRAY)), items = Some(ItemsRoot(schema)))

  /**
    * Convenience method for creating schemas based on a union type of the listed schemas (aka oneOf)
    */
  def schemaFromUnionType(schemas: List[SchemaRoot]) = SchemaRoot(oneOf = Some(schemas))

  // -------
  // Model objects
  // -------

  type PositiveInteger = Int
  type SchemaArray = List[SchemaRoot]
  type StringArray = List[String]

  case class SchemaRoot($schema: Option[String] = None,
                        id: Option[String] = None,
                        title: Option[String] = None,
                        description: Option[String] = None,
                        definitions: Option[List[Field]] = None,
                        properties: Option[List[Field]] = None,
                        `type`: Option[JsType] = None,
                        enum: Option[List[String]] = None,
                        oneOf: Option[SchemaArray] = None,
                        anyOf: Option[SchemaArray] = None,
                        allOf: Option[SchemaArray] = None,
                        not: Option[SchemaRoot] = None,
                        required: Option[StringArray] = None,
                        items: Option[Items] = None,
                        format: Option[JsonFormat] = None,
                        minimum: Option[Double] = None,
                        maximum: Option[Double] = None,
                        exclusiveMinimum: Option[Boolean] = None,
                        exclusiveMaximum: Option[Boolean] = None,
                        $ref: Option[String] = None,
                       ) {

    def toJson: JsValue = Json.toJson(this)

    def toJsonString: String = Json.prettyPrint(toJson)

    // Convenience method since any/all of are treated roughly the same by the generated code
    def multiOf: Option[SchemaArray] = anyOf.orElse(allOf)

    def justDefinitions = SchemaRoot(definitions = this.definitions)
  }

  object SchemaRoot {
    def apply(json: JsValue): SchemaRoot = {
      SchemaRoot()
    }

    implicit val jsonFormat: OFormat[SchemaRoot] = Json.using[Json.WithDefaultValues].format[SchemaRoot]

  }

  sealed trait JsType

  case class SimpleJsType(x: SimpleType) extends JsType

  case class ListSimpleJsType(x: List[SimpleType]) extends JsType

  object JsType {
    implicit val jsonFormat: OFormat[JsType] = derived.oformat[JsType]()

  }

  sealed trait SimpleType
    extends EnumEntry {
  }

  object SimpleType
    extends Enum[SimpleType]
      with PlayInsensitiveJsonEnum[SimpleType] {

    def values: immutable.IndexedSeq[SimpleType] = findValues

    case object STRING extends SimpleType

    case object NUMBER extends SimpleType

    case object BOOLEAN extends SimpleType

    case object ARRAY extends SimpleType

    case object OBJECT extends SimpleType

    case object NULL extends SimpleType

  }

  case class Field(name: String, schema: SchemaRoot)

  object Field {
    implicit val jsonFormat: OFormat[Field] = Json.format[Field]

  }

  sealed trait Items

  case class ItemsRoot(x: SchemaRoot) extends Items

  case class ItemsSchemaArray(x: List[SchemaRoot]) extends Items

  object Items {
    implicit val jsonFormat: OFormat[Items] = derived.oformat[Items]()

  }

  sealed trait JsonFormat
    extends EnumEntry {
    def name: String
  }

  object JsonFormat
    extends Enum[JsonFormat]
      with PlayInsensitiveJsonEnum[JsonFormat] {

    def values: immutable.IndexedSeq[JsonFormat] = findValues

    case object Unknown extends JsonFormat {
      val name = "unknown"
    }

    case object Int64 extends JsonFormat {
      val name = "int64"
    }

    case object Int32 extends JsonFormat {
      val name = "int32"
    }

    case object Int16 extends JsonFormat {
      val name = "int16"
    }

    case object Int8 extends JsonFormat {
      val name = "int8"
    }

    case object Double extends JsonFormat {
      val name = "double"
    }

    case object Single extends JsonFormat {
      val name = "single"
    }

    case object Uuid extends JsonFormat {
      val name = "uuid"
    }

    case object DateTime extends JsonFormat {
      val name = "date-time"
    }

  }

}


