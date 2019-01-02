package pme123.form.client.form

import com.softwaremill.quicklens._
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIPropertyStore extends Logging {

  import UIStore.supportedLangs

  val uiState: UIFormStore.UIState = UIFormStore.uiState

  def changeElementType(elementTypeStr: String): Unit = {
    info(s"PropertyUIStore: changeElementType $elementTypeStr")
    uiState.selectedElement.value.value.elementTypeVar.value = ElementType.withNameInsensitive(elementTypeStr)
    SemanticUI.initElements()
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
    SemanticUI.initElements()
  }


  def addEntry(): Unit = {
    info(s"FormUIStore: addElementEntry")
    uiElem.elemEntriesVar.value = UIElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries :+ UIElementEntry(ElementEntry())
    )
  }

  def deleteEntry(entry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: deleteEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .filter(_ != entry)
    )
  }

  def copyEntry(entry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: copyEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[UIElementEntry])((a, b) =>
          if (b.ident == entry.ident)
            a :+ b :+ b.copy(ident = ElementEntry.ident)
          else
            a :+ b
        )
    )
  }

  def moveEntry(draggedEntry: UIElementEntry, moveToEntry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: moveEntry ${draggedEntry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(hasEntries = true,
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[UIElementEntry])((a, b) =>
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
