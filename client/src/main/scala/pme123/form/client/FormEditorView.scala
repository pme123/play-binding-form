package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client.services.DragDrop

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
    <div class="ui four column doubling stackable grid">
      <div class="ten wide column">
        <div class="ui basic segment">
          <div class="ui form">
            {//
            editorHeader.bind}{//
            editorContent.bind}
          </div>
        </div>
      </div>
      <div class="six wide column">
        {//
        PropertyMenu.create.bind //
        }
      </div>
    </div>

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val editorHeader: Binding[HTMLElement] =
    <div class="ui borderless menu">
      <div class="ui item">
        <h3 class="header">
        <i class="edit outline icon"></i> &nbsp; &nbsp;
        Form Editor</h3>
        </div>
      <div class="ui right item">
        <button class="ui circular blue icon button"
                data:data-tooltip="Add Form Element"
                onclick={_: Event =>
                  FormUIStore.addFormElement()}>
          <i class="add icon"></i>
        </button>
      </div>
    </div>
  @dom
  private lazy val editorContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- FormUIStore.uiState.formElements) yield element(elem).bind}
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {

    val uiElem = uiElemVar.bind
    val uiSelElem = PropertyUIStore.uiState.selectedElement.bind
    <div class={s"${uiElem.wideClass} wide column"}
         ondrop={ev: DragEvent =>
           DragDrop.drop(uiElemVar)(ev)}
         ondragover={ev: DragEvent =>
           DragDrop.allowDrop(ev)}>
      <div class={s"ui ${selectedClass(uiElem, uiSelElem.value)} card"}
           ondragstart={_: DragEvent =>
             DragDrop.drag(uiElemVar)}
           draggable="true">
        {BaseElementDiv(uiElem).bind}<div class="extra content">
        <div class="right floated">

          <button class="mini ui circular blue icon button"
                  data:data-tooltip="Edit Form Element"
                  onclick={_: Event =>
                    FormUIStore.changeSelectedElement(uiElemVar)}>
            <i class="edit icon"></i>
          </button>
          <button class="mini ui circular grey icon button"
                  data:data-tooltip="Copy Form Element"
                  onclick={_: Event =>
                    FormUIStore.copySelectedElement(uiElemVar)}>
            <i class="copy icon"></i>
          </button>
          <button class="mini ui circular grey icon button"
                  data:data-tooltip="Move Form Element (use drag'n'drop)"
                  onclick={_: Event =>
                    FormUIStore.copySelectedElement(uiElemVar)}>
            <i class="hand spock icon"></i>
          </button>
          <button class="mini ui circular red icon button"
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
