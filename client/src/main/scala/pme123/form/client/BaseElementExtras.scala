package pme123.form.client

import pme123.form.client.form.{UIExtraProperties, UIFormElem}
import pme123.form.client.services.I18n
import pme123.form.shared.ElementType.{CHECKBOX, DIVIDER, DROPDOWN, TEXTAREA, TEXTFIELD, TITLE}
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, INPUT_TYPE, ROWS, SIZE_CLASS}
import pme123.form.shared.InputType.NUMBER
import pme123.form.shared.TextType.{LABEL, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

object BaseElementExtras {

  def extras(elementType: ElementType, extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
    elementType match {
      case DROPDOWN =>
        DropdownExtras.extras(extraProperties)
      case TEXTFIELD =>
        TextFieldExtras.extras(extraProperties)
      case TEXTAREA =>
        TextAreaExtras.extras(extraProperties)
      case CHECKBOX =>
        CheckboxExtras.extras(extraProperties)
      case TITLE =>
        TitleExtras.extras(extraProperties)
      case DIVIDER =>
        DividerExtras.extras(extraProperties)
      case _ => Nil
    }
  }

  object DropdownExtras {

    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      val value = extraProperties.valueFor(CLEARABLE).value
      Seq(UIExtraPropValue(
        CLEARABLE, UIFormElem(
          BaseElement(CLEARABLE.entryName,
            CHECKBOX,
            texts = ElementTexts(
              Some(ElementText.label(Map(DE -> "Wert löschen", EN -> "Clearable"))),
              None,
              Some(ElementText.tooltip(Map(DE -> "Auswählen wenn kein Wert möglich ist ", EN -> "Check this if no value should be possible")))
            ),
            value = Some(value),
            extras = ExtraProperties(CHECKBOX),

          ), Some(str =>
            extraProperties.valueFor(CLEARABLE).value = str
          ))))
    }
  }

  object TextFieldExtras {
    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      val value = extraProperties.valueFor(INPUT_TYPE).value

      Seq(UIExtraPropValue(
        INPUT_TYPE, UIFormElem(BaseElement(INPUT_TYPE.entryName,
          DROPDOWN,
          texts = ElementTexts(
            Some(ElementText.label(Map(DE -> "Input Typ", EN -> "Input Type"))),
            None,
            Some(ElementText(TOOLTIP, Map(DE -> "Art des Inputs", EN -> "Type of the Input")))
          ),
          ExtraProperties(DROPDOWN),
          elemEntries = I18n.enumEntries(InputType.values),
          value = Some(value),
        ), Some(str =>
          extraProperties.valueFor(INPUT_TYPE).value = str
        )
        )))
    }
  }

  object TextAreaExtras {
    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      val value = extraProperties.valueFor(INPUT_TYPE).value

      Seq(UIExtraPropValue(
        ROWS, UIFormElem(BaseElement(ROWS.entryName,
          TEXTFIELD,
          texts = ElementTexts(
            Some(ElementText.label(Map(DE -> "Zeilen", EN -> "Rows"))),
            None,
            Some(ElementText(TOOLTIP, Map(DE -> "Anzahl Reihen des Text Bereichs", EN -> "Number of Rows of the Text Area")))
          ),
          ExtraProperties(TEXTFIELD)
            .modifyProperty(INPUT_TYPE, NUMBER.key),
          value = Some(value),
        ), Some(str =>
          extraProperties.valueFor(ROWS).value = str
        )
        )))
    }
  }

  object CheckboxExtras {
    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      val value = extraProperties.valueFor(CHECKBOX_TYPE).value
      Seq(UIExtraPropValue(
        CHECKBOX_TYPE, UIFormElem(BaseElement(CHECKBOX_TYPE.entryName,
          DROPDOWN,
          texts = ElementTexts(
            Some(ElementText.label(Map(DE -> "Art", EN -> "Type"))),
            None,
            Some(ElementText(TOOLTIP, Map(DE -> "Art der Check-Box", EN -> "Type of the Checkbox")))
          ),
          elemEntries = I18n.enumEntries(CheckboxType.values),
          extras = ExtraProperties(DROPDOWN),
          value = Some(value),
        ), Some(str =>
          extraProperties.valueFor(CHECKBOX_TYPE).value = str
        )
        )))
    }
  }

  object TitleExtras {


    def sizeElem(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): UIExtraPropValue = {
      val value = extraProperties.valueFor(SIZE_CLASS).value
      UIExtraPropValue(
        SIZE_CLASS,
        UIFormElem(BaseElement(SIZE_CLASS.entryName,
          DROPDOWN,
          texts = ElementTexts(
            Some(ElementText(LABEL, Map(DE -> "Grösse", EN -> "Size"))),
            None,
            Some(ElementText(TOOLTIP, Map(DE -> "Grösse des Titels (huge, big, medium, small, tiny)", EN -> "Size of the title (huge, big, medium, small, tiny)")))
          ),
          value = Some(value),
          extras = ExtraProperties(DROPDOWN),
          elemEntries = I18n.enumEntries(SizeClass.values),
        ),
          Some(str =>
            extraProperties.valueFor(SIZE_CLASS).value = str
          )))
    }

    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      Seq(sizeElem(extraProperties))
    }
  }

  object DividerExtras {
    def extras(extraProperties: UIExtraProperties)(implicit supportedLangs: Seq[Language]): Seq[UIExtraPropValue] = {
      Seq(TitleExtras.sizeElem(extraProperties))
    }
  }

}

case class UIExtraPropValue(extraProp: ExtraProp, uiFormElem: UIFormElem)