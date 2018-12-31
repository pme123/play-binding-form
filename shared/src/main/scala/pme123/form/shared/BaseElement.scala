package pme123.form.shared

import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.LayoutWide.EIGHT
import pme123.form.shared.services.Language

import scala.util.Random

case class BaseElement(ident: String,
                       elementType: ElementType,
                       dataType: DataType,
                       texts: ElementTexts,
                       extras: ExtraProperties = ExtraProperties(),
                       value: Option[String] = Some(""),
                       required: Boolean = false,
                       inline: Boolean = false,
                       readOnly: Boolean = false,
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: ElementEntries = ElementEntries(),
                       validations: Validations = Validations(),
                      ) {

  val hasTexts: Boolean = texts.hasTexts
  val hasExtras: Boolean = extras.hasExtras
  val hasEntries: Boolean = elemEntries.hasEntries
  val hasValidations: Boolean = validations.hasValidations

  lazy val inlineClass: String = if(inline) "inline" else ""

}

object BaseElement {

  def ident(elementType: ElementType) = s"${elementType.entryName}-${Random.nextInt(100000)}"

  def apply(elementType: ElementType)
           (implicit supportedLangs: Seq[Language]): BaseElement = {
    BaseElement(
      ident(elementType),
      elementType,
      DataTypes.defaultDataType(elementType),
      readOnly = elementType.readOnly,
      texts = ElementTexts(elementType),
      extras = ExtraProperties(elementType),
      elemEntries = ElementEntries(elementType),
      validations = Validations(elementType),
    )
  }

  implicit val jsonFormat: OFormat[BaseElement] = Json.using[Json.WithDefaultValues].format[BaseElement]

}







