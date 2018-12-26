package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client.services.{I18n, SemanticUI, UIStore}
import pme123.form.shared._
import pme123.form.shared.services.SPAExtensions.StringPos

private[client] object FormPreviewView
  extends MainView {

  val link: String = "preview"

  val persistForm = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {persistFormDiv.bind}{//
      previewHeader.bind}{//
      previewContent.bind}{//
      initFields.bind //
      }<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************


  @dom
  private lazy val previewHeader: Binding[HTMLElement] = {
    <div class="ui borderless menu">
      <div class="ui item">
        <h3 class="header">
          <i class="newspaper outline icon"></i> &nbsp; &nbsp;
          Form Preview</h3>
      </div>
      <div class="ui right item">
        <button class="ui circular show-valid icon submit button"
                data:data-tooltip="Validate Form"
                onclick={_: Event =>
                  persistForm.value = true}>
          <i class="check icon"></i>
        </button>
        &nbsp;
        &nbsp;
        <button class="ui circular icon button"
                data:data-tooltip="Export Form as JSON"
                onclick={_: Event =>
                  FormExporter.exportForm()}>
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
    <div class={s"${uiElem.wideClass} wide column"}>
      {BaseElementDiv(uiElem).bind}
    </div>
  }

  @dom
  private lazy val initFields = {
    UIRoute.route.state.bind
    val activeLang = UIStore.uiState.activeLanguage.bind
    val fields = UIFormStore.uiState.formElements.value
      .map { eV =>
        val elem = eV.value.elem
        elem.ident -> SemanticField(elem.ident,
          !elem.required,
          elem.validations.rules
            .filter(_.enabled)
            .map(v =>
              SemanticRule(v.semanticType.toCamelCase, I18n(activeLang, v.validationType.promptI18nKey, v.params.values: _*))
            ) ++ (if (elem.required) Seq(SemanticRule("empty", I18n(activeLang, "enum.validation-type.empty.prompt"))) else Nil)
        )
      }.toMap
    SemanticUI.initForm(SemanticForm(fields = fields))
      <span/>
  }

  @dom
  private lazy val persistFormDiv: Binding[HTMLElement] = {
    val doPersist = persistForm.bind
    if (doPersist)
      <div>
        {persistForm.value = false
      FormServices.persistForm(
        FormExporter.createForm
      ).bind}
      </div>
    else
        <span/>
  }

}
