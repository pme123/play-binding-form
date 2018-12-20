package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.{I18n, SemanticUI, UIStore}
import pme123.form.shared.{SemanticField, SemanticForm, SemanticRule}

import scala.util.matching.Regex

private[client] object FormPreviewView
  extends MainView {

  val hashRegex: Regex = """#preview""".r

  def name: String = "preview"

  val link: String = name

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {//
      previewContent.bind}{//
      initFields.bind //
      }<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************
  @dom
  private lazy val previewContent: Binding[HTMLElement] =
  <div class="ui grid">
    {for (elem <- UIFormStore.uiState.formElements) yield element(elem).bind //
    }
    <div class="sixteen wide column">
      <div class="ui divider"></div>
    <div class="items">
      <div class="item">
        <div class="extra">
          <div class="ui right floated submit button">Submit</div>
          <div class="show-valid">
            <i class="big green check icon"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
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
          elem.validations.toSeq
            .flatMap(_.rules)
            .filter(_.enabled)
            .map(v =>
              SemanticRule(v.semanticType, I18n(activeLang, v.validationType.promptI18nKey, v.params.values: _*))
            ) ++ (if(elem.required) Seq(SemanticRule("empty", I18n(activeLang, "enum.validation-type.empty.prompt"))) else Nil)
        )
      }.toMap
    SemanticUI.initForm(SemanticForm(fields = fields))
      <span/>
  }

}
