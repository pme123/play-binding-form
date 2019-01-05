package pme123.form.client.form

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.client.services.I18n
import pme123.form.shared.services.Language
import pme123.form.shared.services.SPAExtensions.StringPos
import pme123.form.shared.{FormContainer, SemanticField, SemanticRule}

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
        val uiElem = eV.value
        val ident = uiElem.identVar.value
        val required = uiElem.requiredVar.value
        ident -> SemanticField(ident,
          !required,
          uiElem.validationsVar.value.rules.value
            .filter(_.enabled.value)
            .map{v =>
              val vRule = v.toValidationRule
              SemanticRule(vRule.semanticType.toCamelCase, I18n(activeLang, v.validationType.promptI18nKey, vRule.params.values: _*))
            } ++ (if (required) Seq(SemanticRule("empty", I18n(activeLang, "enum.validation-type.empty.prompt"))) else Nil)
        )
      }.toMap
}
