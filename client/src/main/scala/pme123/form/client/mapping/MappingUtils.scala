package pme123.form.client.mapping

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.client.data.UIDataStore.VarDataContainer
import pme123.form.client.form.{FormUtils, UIFormElem, VarFormContainer}
import pme123.form.client.mapping.UIMappingStore.UIMappingEntry
import pme123.form.client.services.SemanticUI
import pme123.form.shared.services.Language
import pme123.form.shared.{MappingContainer, SemanticField, SemanticForm}

object MappingUtils {

  def exportMapping(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createMapping).toString())
    tab.focus()
  }

  def createMapping(form: VarFormContainer,
                    data: VarDataContainer): Unit = {

    UIMappingStore.uiState.mapping.value.autoMap(
      form, data
    )
    SemanticUI.initElements()
  }

  def createMapping: MappingContainer = {
    UIMappingStore.uiState.mapping.value.toMapping
  }

  def initFields(activeLang: Language, elems: Seq[Var[UIFormElem]], mappings: Seq[Var[UIMappingEntry]]): Unit = {
    val fieldRules = FormUtils.semanticFields(elems)(activeLang)
    val dataRules = mappings
      .map { mV =>
        val elemId = dataDropdownIdent(mV.value.uiFormElem.value)
        elemId -> SemanticField(elemId, optional = false, Seq(SemanticUI.emptyRule(activeLang)))
      }.toMap

    SemanticUI.initForm(SemanticForm(fields = fieldRules ++ dataRules))
  }

  def dataDropdownIdent(uiElem: UIFormElem): String = {
    s"mapping-data-${uiElem.identVar.value}"
  }
}
