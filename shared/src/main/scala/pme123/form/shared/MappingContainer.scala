package pme123.form.shared

import play.api.libs.json.{Json, OFormat}

import scala.util.Random

case class MappingContainer(ident: String,
                            formIdent: String,
                            dataIdent: String,
                            mappings: Seq[MappingEntry] = Nil) {

}

object MappingContainer {

  def defaultIdent = s"mapping-${Random.nextInt(1000)}"

  implicit val jsonFormat: OFormat[MappingContainer] = Json.format[MappingContainer]
}

case class GetMappingContainer(ident: String,
                              form: FormContainer,
                              data: DataContainer,
                              mappings: Seq[MappingEntry] = Nil) {

}

object GetMappingContainer {

  implicit val jsonFormat: OFormat[GetMappingContainer] = Json.format[GetMappingContainer]
}

case class MappingEntry(formIdent: String,
                        dataIdent: Option[String] = None,
                       )

object MappingEntry {

  implicit val jsonFormat: OFormat[MappingEntry] = Json.format[MappingEntry]
}