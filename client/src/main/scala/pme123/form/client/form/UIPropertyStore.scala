package pme123.form.client.form

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
    val elementType = ElementType.withNameInsensitive(elementTypeStr)
    changeActivePropElem(
      UIFormElem(
        BaseElement(elementType).copy(texts = uiElem.textsVar.value),
        uiElem.changeEvent
      )
    )
  }

  def changeIdent(ident: String): Unit = {
    info(s"PropertyUIStore: changeIdent $ident")
    uiElem.identVar.value = ident
  }

  def changeDefaultValue(defaultValue: String): Unit = {
    info(s"PropertyUIStore: changeDefaultValue $defaultValue")
    uiElem.valueVar.value = Some(defaultValue)
  }

  private def uiElem = {
    uiState.selectedElement.value.value
  }

  def changeLayoutWide(layoutWide: String): Unit = {
    info(s"PropertyUIStore: changeLayoutWide $layoutWide")
    uiElem.layoutWideVar.value = LayoutWide.withNameInsensitive(layoutWide)
  }

  def changeRequired(required: String): Unit = {
    info(s"PropertyUIStore: changeRequired $required")
    uiElem.requiredVar.value = required.toBoolean
  }

  def changeText(language: Language, textType: TextType)(text: String): Unit = {
    info(s"PropertyUIStore: changeText $language $textType $text")
    textType match {
      case LABEL =>
        uiElem.textsVar.value =
          uiElem.textsVar.value.modify(_.label.each.texts.at(language))
          .setTo(text)
      case PLACEHOLDER =>
        uiElem.textsVar.value =
          uiElem.textsVar.value.modify(_.placeholder.each.texts.at(language))
          .setTo(text)
      case TOOLTIP =>
        uiElem.textsVar.value =
          uiElem.textsVar.value.modify(_.tooltip.each.texts.at(language))
          .setTo(text)
    }
  }


  def addEntry(): Unit = {
    info(s"FormUIStore: addElementEntry")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries :+ ElementEntry()
    )
  }

  def changeEntryText(language: Language, ident: String)(text: String): Unit = {
    info(s"PropertyUIStore: changeEntry $language $ident $text")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[ElementEntry])((a, b) =>
          if (b.ident == ident)
            a :+ b.modify(_.label.texts)
              .setTo(b.label.texts.updated(language, text))
          else
            a :+ b
        )
    )

  }

  def changeEntryKey(ident: String)(key: String): Unit = {
    info(s"PropertyUIStore: changeEntryIdent $key $ident")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[ElementEntry])((a, b) =>
          if (b.ident == ident)
            a :+ b.modify(_.key)
              .setTo(key)
          else
            a :+ b
        )
    )
  }

  def deleteEntry(entry: ElementEntry): Unit = {
    info(s"PropertyUIStore: deleteEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .filter(_ != entry)
    )
  }

  def copyEntry(entry: ElementEntry): Unit = {
    info(s"PropertyUIStore: copyEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[ElementEntry])((a, b) =>
          if (b.ident == entry.ident)
            a :+ b :+ b.copy(ident = ElementEntry.ident)
          else
            a :+ b
        )
    )
  }

  def moveEntry(draggedEntry: ElementEntry, moveToEntry: ElementEntry): Unit = {
    info(s"PropertyUIStore: moveEntry ${draggedEntry.ident}")
    uiElem.elemEntriesVar.value = ElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[ElementEntry])((a, b) =>
          if (b.ident == moveToEntry.ident)
            a :+ b :+ draggedEntry
          else if (b.ident == draggedEntry.ident)
            a
          else
            a :+ b
        )
    )
    SemanticUI.initElements()
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
    uiElem.extrasVar.value =

    uiElem.extrasVar.value.modify(_.propValues)
      .setTo {
        uiElem.extrasVar.value.propValues
          .foldLeft(Seq.empty[ExtraPropValue])((a, b) =>
            if (b.extraProp == extraProp)
              a :+ b.modify(_.value).setTo(Some(text))
            else
              a :+ b
          )
      }
  }


  def changeValidationEnabled(vRule: ValidationRule)(enabled: String): Unit = {
    info(s"PropertyUIStore: changeValidationEnabled $vRule $enabled")
    uiElem.validationsVar.value = Validations(hasValidations = true,
      uiElem.validationsVar.value.rules
        .foldLeft(Seq.empty[ValidationRule])((a, b) =>
          if (b.validationType == vRule.validationType)
            a :+ b.modify(_.enabled).setTo(enabled.toBoolean)
          else
            a :+ b
        )
    )
    SemanticUI.initElements()

  }

  def changeValidationRule(vRule: ValidationRule, param: String)(value: String): Unit = {
    info(s"PropertyUIStore: changeValidationRule $vRule $param")

    uiElem.validationsVar.value = Validations(hasValidations = true,
      uiElem.validationsVar.value.rules
        .foldLeft(Seq.empty[ValidationRule])((a, b) =>
          if (b.validationType == vRule.validationType)
            a :+ b.modify(_.params).setTo(vRule.changeParam(param, value))
          else
            a :+ b
        )
    )
    SemanticUI.initElements()

  }


}
