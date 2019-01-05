package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client._

private[client] object FormEditorView
  extends MainView {

  val link = "editor"
  override val domain = "form"
  val icon = "edit outline"

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui two column stackable grid">
      <div class="ten wide column">
        <div class="ui segment">
          <div class="ui form">
          {//
          header(UIFormStore.uiState.identVar, Some(UIFormStore.changeIdent), headerButtons).bind}{//
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
  private lazy val headerButtons: Binding[HTMLElement] = {
    <button class="ui circular blue icon button"
            data:data-tooltip="Add Form Element"
            onclick={_: Event =>
              UIFormStore.addFormElement()}>
      <i class="add icon"></i>
    </button>
  }

  @dom
  private lazy val editorContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- UIFormStore.uiState.formElements) yield element(elem).bind}
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {
    val uiElem = uiElemVar.bind
    val elemType = uiElem.elementTypeVar.bind
    val uiSelElem = UIElemEntriesStore.uiState.selectedElement.bind
    val layoutWide = uiElem.layoutWideVar.bind
    val wideClass: String = layoutWide.entryName.toLowerCase

    <div class={s"$wideClass wide column"}
         ondrop={ev: DragEvent =>
           UIFormElemDragDrop.drop(uiElemVar)(ev)}
         ondragover={ev: DragEvent =>
           UIFormElemDragDrop.allowDrop(uiElemVar)(ev)}>
      <div class={s"ui ${selectedClass(uiElem, uiSelElem.value)} card"}
           draggable="true"
           ondragstart={_: DragEvent =>
             UIFormElemDragDrop.drag(uiElemVar)}>
        {BaseElementDiv(uiElem).bind}<div class="extra content">
        <div class="right floated">

          <button class="mini ui circular blue icon button"
                  data:data-tooltip="Edit Form Element"
                  onclick={_: Event =>
                    UIFormStore.changeSelectedElement(uiElemVar)}>
            <i class="edit icon"></i>
          </button>
          <button class="mini ui circular grey icon button"
                  data:data-tooltip="Copy Form Element"
                  onclick={_: Event =>
                    UIFormStore.copySelectedElement(uiElemVar)}>
            <i class="copy icon"></i>
          </button>
          <button class="mini ui circular grey icon button"
                  data:data-tooltip="Move Form Element (use drag'n'drop)">
            <i class="hand spock icon"></i>
          </button>
          <button class="mini ui circular red icon button"
                  data:data-tooltip="Delete Form Element"
                  onclick={_: Event =>
                    UIFormStore.deleteSelectedElement(uiElemVar)}>
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
