package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared.PropTabType.PROPERTIES
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language.{DE, EN}
import pme123.form.shared.services.{Language, Logging}

import scala.language.implicitConversions


object FormUIStore extends Logging {

  val supportedLangs = Seq(EN, DE)

  val uiState = UIState()

  def addFormElement(): Var[UIFormElem] = {
    info(s"FormUIStore: addFormElement")
    val elem = Var(UIFormElem())
    uiState.formElements.value += elem
    changeSelectedElement(elem)
    SemanticUI.initPlaceholders()
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
      SemanticUI.initDropdowns()
    }
  }

  def changeActivePropTab(propTabType: PropTabType): Unit = {
    info(s"FormUIStore: changeActivePropTab $propTabType")
    uiState.activePropTab.value = propTabType
    SemanticUI.initDropdowns()
  }

  def changeLanguage(language: String): Unit = {
    info(s"FormUIStore: changeLanguage $language")
    uiState.activeLanguage.value = Language.withNameInsensitive(language)
    SemanticUI.initPlaceholders()
  }

  case class UIState(formElements: Vars[Var[UIFormElem]],
                     selectedElement: Var[Var[UIFormElem]],
                     activePropTab: Var[PropTabType],
                     activeLanguage: Var[Language],
                    )

  object UIState {
    def apply(): UIState = {
      val defaultElem = Var(UIFormElem())
      SemanticUI.initPlaceholders()

      UIState(
        Vars(defaultElem),
        Var(defaultElem),
        Var(PROPERTIES),
        Var(EN),
      )
    }
  }


}

object PropertyUIStore extends Logging {

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
      uiElem.modify(_.elem.defaultValue)
        .setTo(defaultValue)
    )
  }

  def changeElementType(elementTypeStr: String): Unit = {
    info(s"PropertyUIStore: changeElementType $elementTypeStr")
    val uiElem = uiState.selectedElement.value.value
    val elementType = ElementType.withNameInsensitive(elementTypeStr)
    changeUIFormElem(
      UIFormElem(
        BaseElement(elementType, FormUIStore.supportedLangs),
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


  def changeText(language: Language, textType: TextType, text: String): Unit = {
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
        SemanticUI.initPlaceholders()
        uiElem.modify(_.elem.texts.tooltip.texts.at(language))
          .setTo(text)
    }
    changeUIFormElem(newElem)
  }

  private def changeUIFormElem(uiFormElem: UIFormElem): Unit = {
    uiState.selectedElement.value.value = uiFormElem
  }

  def changeExtraProp(extraProp: ExtraProp)(text: String): Unit = {
    info(s"PropertyUIStore: changeExtraProp $extraProp $text")
    val uiElem = uiState.selectedElement.value.value
    val newElem =
      uiElem.modify(_.elem.extras.at(extraProp).defaultValue)
        .setTo(text)

    changeUIFormElem(newElem)
  }

}
