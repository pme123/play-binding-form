package pme123.form.client

import com.softwaremill.quicklens._
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.{Language, Logging}

object UIPropertyStore extends Logging {

  import UIStore.supportedLangs

  val uiState: UIFormStore.UIState = UIFormStore.uiState

  def changeElementType(elementTypeStr: String): Unit = {
    info(s"PropertyUIStore: changeElementType $elementTypeStr")
    val uiElem = uiState.selectedElement.value.value
    val elementType = ElementType.withNameInsensitive(elementTypeStr)
    changeActivePropElem(
      UIFormElem(
        BaseElement(elementType).copy(texts = uiElem.elem.texts),
        uiElem.changeEvent
      )
    )
  }

  def changeIdent(ident: String): Unit = {
    info(s"PropertyUIStore: changeIdent $ident")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem.modify(_.elem.ident)
        .setTo(ident)
    )
  }

  def changeDefaultValue(defaultValue: String): Unit = {
    info(s"PropertyUIStore: changeDefaultValue $defaultValue")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem.modify(_.elem.value)
        .setTo(Some(defaultValue))
    )
  }

  def changeLayoutWide(layoutWide: String): Unit = {
    info(s"PropertyUIStore: changeLayoutWide $layoutWide")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem.modify(_.elem.layoutWide)
        .setTo(LayoutWide.withNameInsensitive(layoutWide))
    )
  }

  def changeRequired(required: String): Unit = {
    info(s"PropertyUIStore: changeRequired $required")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem.modify(_.elem.required)
        .setTo(required.toBoolean)
    )
  }

  def changeText(language: Language, textType: TextType)(text: String): Unit = {
    info(s"PropertyUIStore: changeText $language $textType $text")
    val uiElem = uiState.selectedElement.value.value
    val newElem = textType match {
      case LABEL =>
        uiElem.modify(_.elem.texts.label.each.texts.at(language))
          .setTo(text)
      case PLACEHOLDER =>
        uiElem.modify(_.elem.texts.placeholder.each.texts.at(language))
          .setTo(text)
      case TOOLTIP =>
        uiElem.modify(_.elem.texts.tooltip.each.texts.at(language))
          .setTo(text)
    }
    changeUIFormElem(newElem)
  }


  def addEntry(): Unit = {
    info(s"FormUIStore: addElementEntry")
    val uiElem = uiState.selectedElement.value.value
    changeActivePropElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo(uiElem.elem.elemEntries.entries :+ ElementEntry()))
  }

  def changeEntryText(language: Language, ident: String)(text: String): Unit = {
    info(s"PropertyUIStore: changeEntry $language $ident $text")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo {
          uiElem.elem.elemEntries.entries
            .foldLeft(Seq.empty[ElementEntry]) { (a, b) =>
              if (b.ident == ident)
                a :+ b.modify(_.label.texts)
                  .setTo(b.label.texts.updated(language, text))
              else
                a :+ b
            }
        })
  }

  def changeEntryKey(ident: String)(key: String): Unit = {
    info(s"PropertyUIStore: changeEntryIdent $key $ident")
    val uiElem = uiState.selectedElement.value.value
    changeUIFormElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo {
          uiElem.elem.elemEntries.entries
            .foldLeft(Seq.empty[ElementEntry]) { (a, b) =>
              if (b.ident == ident)
                a :+ b.modify(_.key)
                  .setTo(key)
              else
                a :+ b
            }
        })
  }

  def deleteEntry(entry: ElementEntry): Unit = {
    info(s"PropertyUIStore: deleteEntry ${entry.ident}")
    val uiElem = uiState.selectedElement.value.value
    changeActivePropElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo(uiElem.elem.elemEntries.entries.filter(_ != entry))
    )
  }

  def copyEntry(entry: ElementEntry): Unit = {
    info(s"PropertyUIStore: copyEntry ${entry.ident}")
    val uiElem = uiState.selectedElement.value.value
    changeActivePropElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo {
          uiElem.elem.elemEntries.entries
            .foldLeft(Seq.empty[ElementEntry])((a, b) =>
              if (b.ident == entry.ident)
                a :+ b :+ b.copy(ident = ElementEntry.ident)
              else
                a :+ b
            )
        }
    )
  }

  def moveEntry(draggedEntry: ElementEntry, moveToEntry: ElementEntry): Unit = {
    info(s"PropertyUIStore: moveEntry ${draggedEntry.ident}")
    val uiElem = uiState.selectedElement.value.value
    changeActivePropElem(
      uiElem
        .modify(_.elem.elemEntries.entries)
        .setTo {
          uiElem.elem.elemEntries.entries
            .foldLeft(Seq.empty[ElementEntry])((a, b) =>
              if (b.ident == moveToEntry.ident)
                a :+ b :+ draggedEntry
              else if (b.ident == draggedEntry.ident)
                a
              else
                a :+ b
            )
        }
    )
  }

  private def changeActivePropElem(newUIElem: UIFormElem): Unit = {
    changeUIFormElem(newUIElem)
    uiState.activePropElement.value = newUIElem
  }

  private def changeUIFormElem(uiFormElem: UIFormElem): Unit = {
    uiState.selectedElement.value.value = uiFormElem
    SemanticUI.initElements()
  }

  def changeExtraProp(extraProp: ExtraProp)(text: String): Unit = {
    info(s"PropertyUIStore: changeExtraProp $extraProp $text")
    val uiElem = uiState.selectedElement.value.value
    val newElem =
      uiElem.elem.modify(_.extras.propValues)
        .setTo {
          uiElem.elem.extras.propValues
            .foldLeft(Seq.empty[ExtraPropValue])((a, b) =>
              if (b.extraProp == extraProp)
                a :+ b.modify(_.value).setTo(Some(text))
              else
                a :+ b
            )
        }

    changeUIFormElem(UIFormElem(
      newElem,
      uiElem.changeEvent
    ))
  }


  def changeValidationEnabled(vRule: ValidationRule)(enabled: String): Unit = {
    info(s"PropertyUIStore: changeValidationEnabled $vRule $enabled")
    val uiElem = uiState.selectedElement.value.value
    val newElem =
      uiElem.elem.modify(_.validations.rules)
        .setTo {
          uiElem.elem.validations.rules
            .foldLeft(Seq.empty[ValidationRule])((a, b) =>
              if (b.validationType == vRule.validationType)
                a :+ b.modify(_.enabled).setTo(enabled.toBoolean)
              else
                a :+ b
            )
        }
    changeUIFormElem(UIFormElem(
      newElem,
      uiElem.changeEvent
    ))
  }

  def changeValidationRule(vRule: ValidationRule, param: String)(value: String): Unit = {
    info(s"PropertyUIStore: changeValidationRule $vRule $param")
    val uiElem = uiState.selectedElement.value.value
    val newElem =
      uiElem.elem.modify(_.validations.rules)
        .setTo {
          uiElem.elem.validations.rules
            .foldLeft(Seq.empty[ValidationRule])((a, b) =>
              if (b.validationType == vRule.validationType)
                a :+ b.modify(_.params).setTo(vRule.changeParam(param, value))
              else
                a :+ b
            )
        }
    changeUIFormElem(UIFormElem(
      newElem,
      uiElem.changeEvent
    ))
  }


}
