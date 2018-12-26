package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared.services.Language.{DE, EN}
import pme123.form.shared.{BaseElement, DataType, ElementText, ElementTexts}

private[client] object FormEditorView
  extends MainView {

  val link: String = "editor"

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
  private lazy val editorHeader: Binding[HTMLElement] = {
    <div class="ui borderless menu">
      <div class="ui item">
        <h3 class="header">
          <i class="edit outline icon"></i> &nbsp; &nbsp;
          Form Editor</h3>
      </div>
      <div class="ui right item">
        {
        val formId = UIFormStore.uiState.formId.bind
        BaseElementDiv(
        UIFormElem(
          BaseElement(
            "form-ident",
            TEXTFIELD,
            DataType.STRING,
            ElementTexts(
              None,
              Some(ElementText.placeholder(Map(
                DE -> "Form Identität",
                EN -> "Form Identity",
              )))
            ),
            value = Some(formId)),
          Map.empty,
          Some(UIFormStore.changeFormId),
        )).bind}
      </div>
      <div class="ui right item">
        <button class="ui circular blue icon button"
                data:data-tooltip="Add Form Element"
                onclick={_: Event =>
                  UIFormStore.addFormElement()}>
          <i class="add icon"></i>
        </button>
      </div>
    </div>
  }

  @dom
  private lazy val editorContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- UIFormStore.uiState.formElements) yield element(elem).bind}
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {
    val uiElem = uiElemVar.bind
    val uiSelElem = UIPropertyStore.uiState.selectedElement.bind
    <div class={s"${uiElem.wideClass} wide column"}
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
