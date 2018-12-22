package pme123.form.client

import play.api.libs.json.Json
import pme123.form.shared.FormContainer
import org.scalajs.dom.window


object FormExporter {

  def exportForm():Unit = {
    val form = FormContainer(
      UIFormStore.uiState.formElements.value
      .map { eV =>
        eV.value.elem
      }
    )
    println("JSON EXPORT" + Json.toJson(form))
    val tab = window.open("data:text/json", "Json Export")
    tab.document.write(Json.toJson(form).toString())
tab.focus()
  }

}
