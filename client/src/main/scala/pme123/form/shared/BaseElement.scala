package pme123.form.shared

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
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: ElementEntries = ElementEntries(),
                       validations: Validations = Validations(),
                      ) {

  val hasTexts: Boolean = texts.hasTexts
  val hasExtras: Boolean = extras.hasExtras
  val hasEntries: Boolean = elemEntries.hasEntries
  val hasValidations: Boolean = validations.hasValidations

}

object BaseElement {

  def ident(elementType: ElementType) = s"${elementType.entryName}-${Random.nextInt(100000)}"

  def apply(elementType: ElementType)
           (implicit supportedLangs: Seq[Language]): BaseElement = {


    BaseElement(
      ident(elementType),
      elementType,
      DataTypes.defaultDataType(elementType),
      texts = ElementTexts(elementType),
      extras = ExtraProperties(elementType),
      elemEntries = ElementEntries(elementType),
      validations = Validations(elementType),
    )


  }
}







