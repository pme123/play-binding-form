package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared.PropTabType.PROPERTIES
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.language.implicitConversions
import scala.util.Random


object UIFormStore extends Logging {

  val uiState = UIState()

  def changePage(): Unit = {
    uiState.activePropElement.value = uiState.selectedElement.value.value
  }

  def changeForm(form: FormContainer): Unit = {
    info(s"FormUIStore: changeForm ${form.formId}")
    changeFormId(form.formId)
    uiState.formElements.value.clear()
    val seq = form.elems.map(UIFormElem.apply(_))
      .map(Var(_))
    if(seq.nonEmpty){
      uiState.formElements.value ++= seq
      changeSelectedElement(seq.head)
    }else{
      addFormElement()
    }
  }


  def addFormElement(): Var[UIFormElem] = {
    info(s"FormUIStore: addFormElement")
    val elem = Var(UIFormElem())
    uiState.formElements.value += elem
    changeSelectedElement(elem)
    elem
  }

  def deleteSelectedElement(elemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: deleteSelectedElement ${ident(elemVar)}")
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
    info(s"FormUIStore: changeSelectedElement ${ident(elemVar)}")
    if (uiState.selectedElement.value != elemVar) {
      uiState.selectedElement.value = elemVar
      uiState.activePropElement.value = elemVar.value
      changeActivePropTab(PROPERTIES)
      SemanticUI.initElements()
    }
  }

  def copySelectedElement(elemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: copySelectedElement ${ident(elemVar)}")
    val newUIElem = elemVar.value.modify(_.elem.ident)
      .setTo(BaseElement.ident(elemVar.value.elem.elementType))
    val newUIElemVar = Var(
      newUIElem
    )
    uiState.selectedElement.value = newUIElemVar
    uiState.activePropElement.value = elemVar.value
    uiState.formElements.value.insert(
      uiState.formElements.value.indexOf(elemVar) + 1,
      newUIElemVar)
    SemanticUI.initElements()
  }

  def moveElement(draggedElem: Var[UIFormElem], moveToElemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: moveElement ${ident(draggedElem)} to ${ident(moveToElemVar)}")
    uiState.formElements.value.remove(
      uiState.formElements.value.indexOf(draggedElem))

    uiState.formElements.value.insert(
      uiState.formElements.value.indexOf(moveToElemVar) + 1,
      draggedElem)

    changeSelectedElement(draggedElem)
  }


  def changeActivePropTab(propTabType: PropTabType): Unit = {
    info(s"FormUIStore: changeActivePropTab $propTabType")
    uiState.activePropTab.value = propTabType
    uiState.activePropElement.value = uiState.selectedElement.value.value
    SemanticUI.initElements()
  }

  def changeFormId(formId: String): Unit = {
    info(s"FormUIStore: changeFormId $formId")
    uiState.formId.value = formId
  }

  def changeFormIds(formIds: Seq[String]): Unit = {
    info(s"FormUIStore: changeFormIds $formIds")
    uiState.formIds.value.clear()
    uiState.formIds.value ++= formIds
  }

  case class UIState(formElements: Vars[Var[UIFormElem]],
                     selectedElement: Var[Var[UIFormElem]],
                     activePropElement: Var[UIFormElem],
                     activePropTab: Var[PropTabType],
                     formId: Var[String],
                     formIds: Vars[String]
                    )

  object UIState {
    def apply(): UIState = {
      val defaultElem = Var(UIFormElem())

      UIState(
        Vars(defaultElem),
        Var(defaultElem),
        Var(defaultElem.value),
        Var(PROPERTIES),
        Var(s"form-${Random.nextInt(100)}"),
        Vars.empty
      )
    }
  }

  private def ident(uiElemVar: Var[UIFormElem]) = uiElemVar.value.elem.ident


}
