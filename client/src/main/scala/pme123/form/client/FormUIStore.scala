package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.PropTabType.PROPERTIES
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.{Language, Logging}

import scala.language.implicitConversions


object FormUIStore extends Logging {

  val uiState = UIState()

  def addFormElement(): Var[UIFormElem] = {
    info(s"FormUIStore: addFormElement")
    val elem = Var(UIFormElem())
    uiState.formElements.value += elem
    changeSelectedElement(elem)
    elem
  }

  def deleteSelectedElement(elemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: deleteSelectedElement ${elemVar.value}")
    if (uiState.selectedElement.value == elemVar) {
      val newElem = uiState.formElements.value.headOption
        .getOrElse {
          addFormElement()
        }
      changeSelectedElement(newElem)
    }
    uiState.formElements.value -= elemVar
  }

  def changeSelectedElement(elemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: changeSelectedElement ${elemVar.value}")
    if (uiState.selectedElement.value != elemVar) {
      uiState.selectedElement.value = elemVar
      SemanticUI.initElements()
    }
  }

  def changeSelectedElement(uiFormElem: UIFormElem): Unit = {
    info(s"FormUIStore: changeSelectedElement $uiFormElem")
    if (uiState.selectedElement.value.value != uiFormElem) {
      uiState.selectedElement.value.value = uiFormElem
      SemanticUI.initElements()
    }
  }

  def changeActivePropTab(propTabType: PropTabType): Unit = {
    info(s"FormUIStore: changeActivePropTab $propTabType")
    uiState.activePropTab.value = propTabType
    SemanticUI.initElements()
  }

  case class UIState(formElements: Vars[Var[UIFormElem]],
                     selectedElement: Var[Var[UIFormElem]],
                     activePropTab: Var[PropTabType],
                    )

  object UIState {
    def apply(): UIState = {
      val defaultElem = Var(UIFormElem())

      UIState(
        Vars(defaultElem),
        Var(defaultElem),
        Var(PROPERTIES),
      )
    }
  }


}

object PropertyUIStore extends Logging {

  import UIStore.supportedLangs

  val uiState: FormUIStore.UIState = FormUIStore.uiState

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

  def changeElementType(elementTypeStr: String): Unit = {
    info(s"PropertyUIStore: changeElementType $elementTypeStr")
    val uiElem = uiState.selectedElement.value.value
    val elementType = ElementType.withNameInsensitive(elementTypeStr)
    changeUIFormElem(
      UIFormElem(
        BaseElement(elementType),
        uiElem.changeEvent
      )
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

  def changeText(language: Language, textType: TextType)(text: String): Unit = {
    info(s"PropertyUIStore: changeText $language $textType $text")
    val uiElem = uiState.selectedElement.value.value
    val newElem = textType match {
      case LABEL =>
        uiElem.modify(_.elem.texts.label.texts.at(language))
          .setTo(text)
      case PLACEHOLDER =>
        uiElem.modify(_.elem.texts.placeholder.texts.at(language))
          .setTo(text)
      case TOOLTIP =>
        uiElem.modify(_.elem.texts.tooltip.texts.at(language))
          .setTo(text)
    }
    changeUIFormElem(newElem)
  }


  def addElementEntry(): Unit = {
    info(s"FormUIStore: addFormElement")
    val entry = ElementEntry(UIStore.supportedLangs)
    val uiElem = uiState.selectedElement.value.value
    FormUIStore.changeSelectedElement(
      uiElem
        .modify(_.elem.elemEntries)
        .setTo(uiElem.elem.elemEntries.map(es => ElementEntries(es.entries :+ entry)))
    )
  }

  def changeEntry(language: Language, pos: Int)(text: String): Unit = {
    info(s"PropertyUIStore: changeEntry $language $pos $text")
    val uiElem = uiState.selectedElement.value.value
    FormUIStore.changeSelectedElement(
      uiElem
        .modify(_.elem.elemEntries)
        .setTo(
          uiElem.elem.elemEntries.map {
            es =>
              val e = es.entries(pos)
              ElementEntries(
                es.entries.updated(pos,
                  e.modify(_.label.texts)
                    .setTo(e.label.texts.updated(language, text))))
          }))
  }

  def changeEntryIdent(pos: Int)(ident: String): Unit = {
    info(s"PropertyUIStore: changeEntryIdent $pos $ident")
    val uiElem = uiState.selectedElement.value.value
    FormUIStore.changeSelectedElement(
      uiElem
        .modify(_.elem.elemEntries)
        .setTo(
          uiElem.elem.elemEntries.map {
            es =>
              val e = es.entries(pos)
              ElementEntries(
                es.entries.updated(pos,
                  e.modify(_.ident)
                    .setTo(ident)))
          }))

  }

  private def changeUIFormElem(uiFormElem: UIFormElem): Unit = {
    uiState.selectedElement.value.value = uiFormElem
    SemanticUI.initElements()
  }

  def changeExtraProp(extraProp: ExtraProp)(text: String): Unit = {
    info(s"PropertyUIStore: changeExtraProp $extraProp $text")
    val uiElem = uiState.selectedElement.value.value
    val newElem =
      uiElem.elem.modify(_.extras.at(extraProp).value)
        .setTo(Some(text))

    changeUIFormElem(UIFormElem(
      newElem,
      uiElem.changeEvent
    ))
  }

}
