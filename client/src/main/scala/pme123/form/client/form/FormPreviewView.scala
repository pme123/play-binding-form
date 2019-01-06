package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client._
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared._

private[client] object FormPreviewView
  extends MainView {

  val link = "preview"
  override val domain = "form"
  val icon = "newspaper outline"

  val persistForm = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {persistFormDiv.bind}{//
      header(UIFormStore.uiState.identVar, Some(UIFormStore.changeIdent), headerButtons).bind}{//
      previewContent.bind}{//
      initFields.bind //
      }<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************


  @dom
  private lazy val headerButtons: Binding[HTMLElement] = {
    <div class="">
      <button class="ui circular show-valid icon submit button"
              data:data-tooltip="Validate Form"
              onclick={_: Event =>
                SemanticUI.validateForm()}>
        <i class="check icon"></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular icon button"
              data:data-tooltip="Export Form as JSON"
              onclick={_: Event =>
                FormUtils.exportForm()}>
        <i class="sign-out icon"></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular blue icon button"
              data:data-tooltip="Persist Form on Server"
              onclick={_: Event =>
                persistForm.value = true}>
        <i class="save outline icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val previewContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- UIFormStore.uiState.formElements) yield element(elem).bind //
      }
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {
    val uiElem = uiElemVar.bind
    val layoutWide = uiElem.layoutWideVar.bind
    val wideClass: String = layoutWide.entryName.toLowerCase
    println("UIELELMMM: " + uiElem.identVar.value)
    <div class={s"$wideClass wide column"}>
      {BaseElementDiv(uiElem).bind}
    </div>
  }

  @dom
  private lazy val initFields = {
    UIRoute.route.state.watch()
    val activeLang = UIStore.uiState.activeLanguage.bind
    SemanticUI.initForm(SemanticForm(fields = FormUtils.semanticFields(activeLang)))
      <span/>
  }

  @dom
  private lazy val persistFormDiv: Binding[HTMLElement] = {
    val doPersist = persistForm.bind
    if (doPersist)
      <div>
        {persistForm.value = false
      FormServices.persistForm(
        FormUtils.createForm
      ).bind}
      </div>
    else
        <span/>
  }

}
