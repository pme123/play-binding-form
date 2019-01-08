package pme123.form.shared

import play.api.libs.json.{Json, OFormat}
import pme123.form.shared.ElementType.{CHECKBOX, DIVIDER, DROPDOWN, TEXTFIELD, TITLE}
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, INPUT_TYPE, SIZE, SIZE_CLASS}
import pme123.form.shared.ExtraPropValue.ExtraValue

case class ExtraProperties(propValues: Seq[ExtraPropValue] = Nil) {

  def propValue(extraProp: ExtraProp): Option[ExtraValue] =
    propValues.find(_.extraProp == extraProp)
      .map(_.value)

}

object ExtraProperties {

  def apply(elementType: ElementType): ExtraProperties = {
    elementType match {
      case TEXTFIELD =>
        TextFieldElem.extras
      case DROPDOWN =>
        DropdownElem.extras
      case CHECKBOX =>
        CheckboxElem.extras
      case TITLE =>
        TitleElem.extras
      case DIVIDER =>
        DividerElem.extras
      case _ => ExtraProperties()
    }
  }

  object TextFieldElem {
    def extras: ExtraProperties =
      ExtraProperties(Seq(
        ExtraPropValue(
          INPUT_TYPE, INPUT_TYPE.defaultValue
        ), ExtraPropValue(
          SIZE, SIZE.defaultValue
        )))
  }

  object DropdownElem {
    def extras: ExtraProperties = {

      ExtraProperties(Seq(
        ExtraPropValue(
          CLEARABLE, CLEARABLE.defaultValue
        ))
      )
    }
  }

  object CheckboxElem {
    def extras: ExtraProperties = {

      ExtraProperties(Seq(
        ExtraPropValue(
          CHECKBOX_TYPE, CHECKBOX_TYPE.defaultValue
        ))
      )
    }


  }


  object TitleElem {


    def sizeElem = ExtraPropValue(SIZE_CLASS, SIZE_CLASS.defaultValue)

    def extras: ExtraProperties = {
      ExtraProperties(
        Seq(sizeElem)
      )
    }
  }

  object DividerElem {
    def extras: ExtraProperties = {

      ExtraProperties(
        Seq(TitleElem.sizeElem)
      )
    }
  }

  implicit val jsonFormat: OFormat[ExtraProperties] = Json.format[ExtraProperties]


}

case class ExtraPropValue(extraProp: ExtraProp, value: ExtraValue)

object ExtraPropValue {

  type ExtraValue = String

  implicit val jsonFormat: OFormat[ExtraPropValue] = Json.format[ExtraPropValue]

}


