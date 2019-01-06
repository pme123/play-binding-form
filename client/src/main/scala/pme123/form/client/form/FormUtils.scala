package pme123.form.client.form

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.client.services.I18n
import pme123.form.shared.services.Language
import pme123.form.shared.{FormContainer, SemanticField, SemanticRule}
import pme123.form.shared.services.SPAExtensions.StringPos

object FormUtils {

  def exportForm(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createForm).toString())
    tab.focus()
  }

  def createForm: FormContainer =
    UIFormStore.uiState.form.value.toForm

  def semanticFields(implicit activeLang: Language): Map[String, SemanticField] =
    UIFormStore.uiState.formElements.value
      .map { eV =>
        val elem = eV.value.toBaseElement
        elem.ident -> SemanticField(elem.ident,
          !elem.required,
          elem.validations.rules
            .filter(_.enabled)
            .map(v =>
              SemanticRule(v.semanticType.toCamelCase, I18n(activeLang, v.validationType.promptI18nKey, v.params.values: _*))
            ) ++ (if (elem.required) Seq(SemanticRule("empty", I18n(activeLang, "enum.validation-type.empty.prompt"))) else Nil)
        )
      }.toMap
}
