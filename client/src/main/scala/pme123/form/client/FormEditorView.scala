package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client.FormUIStore.UIFormElem

import scala.util.matching.Regex

private[client] object FormEditorView
  extends MainView {

  val hashRegex: Regex = """#editor""".r

  def name: String = "editor"

  val link: String = name

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui form">
      {//
      editorHeader.bind}{//
      editorContent.bind}
    </div>


  // 2. level of abstraction
  // **************************

  @dom
  private lazy val editorHeader: Binding[HTMLElement] = <h1 class="header">
    <i class="edit outline icon"></i> &nbsp; &nbsp;
    Form Editor
    <button class="ui circular blue icon button"
            data:data-tooltip="Add Form Element"
            onclick={_: Event =>
              FormUIStore.addFormElement()}>
      <i class="big add icon"></i>
    </button>
  </h1>

  @dom
  private lazy val editorContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- FormUIStore.uiState.formElements) yield element(elem).bind}
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {

    val uiElem = uiElemVar.bind
    val uiSelElem = PropertyUIStore.uiState.selectedElement.bind
    <div class={s"${uiElem.wideClass} wide column"}>
      <div class={s"ui ${selectedClass(uiElem, uiSelElem.value)} card"}>
       {BaseElementDiv(uiElem.elem).bind}
        <div class="extra content">
          <div class="right floated author">

            <button class="floated right mini ui circular blue mini icon button"
                    data:data-tooltip="Edit Form Element"
                    onclick={_: Event =>
                      FormUIStore.changeSelectedElement(uiElemVar)}>
              <i class="edit icon"></i>
            </button>
            <button class="floated right ui circular red mini icon button"
                    data:data-tooltip="Delete Form Element"
                    onclick={_: Event =>
                      FormUIStore.deleteSelectedElement(uiElemVar)}>
              <i class="trash icon"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  }

  private def selectedClass(uiElem: UIFormElem, selectUIElem: UIFormElem) =
    if (uiElem == selectUIElem)
      "blue"
    else
      ""

}
