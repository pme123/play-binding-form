package pme123.form.client.form

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.PropTabType.PROPERTIES
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.language.implicitConversions
import scala.util.Random


object UIFormStore extends Logging {

  val uiState = UIState()

  def changeForm(form: FormContainer): Var[VarFormContainer] = {
    info(s"FormUIStore: changeForm ${form.ident}")
    uiState.varIdent.value = form.ident
    uiState.formElements.value.clear()
    val seq = form.elems.map(UIFormElem.apply(_))
      .map(Var(_))
    if (seq.nonEmpty) {
      uiState.formElements.value ++= seq
      changeSelectedElement(seq.head)
    } else {
      addFormElement()
    }
    uiState.form.value = VarFormContainer(
      uiState.varIdent,
      uiState.formElements
    )
    SemanticUI.initElements()
    uiState.form
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
    if (uiState.selectedElement.value != elemVar) {
      info(s"FormUIStore: changeSelectedElement ${ident(elemVar)}")
      uiState.selectedElement.value = elemVar
      changeActivePropTab(PROPERTIES)
      SemanticUI.initElements()
    }
  }

  def copySelectedElement(elemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: copySelectedElement ${ident(elemVar)}")
    val newUIElem = elemVar.value.copy()
      newUIElem.varIdent.value = BaseElement.ident(elemVar.value.elementTypeVar.value)
    val newUIElemVar = Var(
      newUIElem
    )
    uiState.selectedElement.value = newUIElemVar
    uiState.formElements.value.insert(
      uiState.formElements.value.indexOf(elemVar) + 1,
      newUIElemVar)
    SemanticUI.initElements()
  }

  def moveElement(draggedElem: Var[UIFormElem], moveToElemVar: Var[UIFormElem]): Unit = {
    info(s"FormUIStore: moveElement ${ident(draggedElem)} to ${ident(moveToElemVar)}")
    val existIndex = uiState.formElements.value.indexOf(draggedElem)
    val newIndex = uiState.formElements.value.indexOf(moveToElemVar)
    uiState.formElements.value.remove(
      existIndex)

    uiState.formElements.value.insert(
      newIndex,
      draggedElem)

    changeSelectedElement(draggedElem)
  }


  def changeActivePropTab(propTabType: PropTabType): Unit = {
    info(s"FormUIStore: changeActivePropTab $propTabType")
    uiState.activePropTab.value = propTabType
    SemanticUI.initElements()
  }

  def changeIdent(ident: String): Unit = {
    info(s"FormUIStore: changeIdent $ident")
    uiState.varIdent.value = ident
  }

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"FormUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }

  def formElement(ident: String): Option[Var[UIFormElem]] = {
    uiState.formElements.value
      .find(_.value.varIdent.value == ident)
  }

  case class UIState(
                      varIdent: Var[String],
                      form: Var[VarFormContainer],
                      formElements: Vars[Var[UIFormElem]],
                      selectedElement: Var[Var[UIFormElem]],
                      activePropTab: Var[PropTabType],
                      idents: Vars[String]
                    ) {

  }

  object UIState {
    def apply(): UIState = {
      val defaultElem = Var(UIFormElem())
      val elems = Vars(defaultElem)
      val varIdent = Var(s"form-${Random.nextInt(100)}")
      UIState(
        varIdent,
        Var(VarFormContainer(varIdent, elems)),
        elems,
        Var(defaultElem),
        Var(PROPERTIES),
        Vars.empty
      )
    }
  }

  private def ident(uiElemVar: Var[UIFormElem]) = uiElemVar.value.varIdent.value


}

case class VarFormContainer(varIdent: Var[String], elems: Vars[Var[UIFormElem]]) {

  def toForm: FormContainer =
  {
    println("TOFORM: " + varIdent.value)
    FormContainer(varIdent.value,
      elems.value.map(_.value.toBaseElement))
  }

}

object VarFormContainer{

}