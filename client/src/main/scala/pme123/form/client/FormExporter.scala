package pme123.form.client

import play.api.libs.json.Json
import pme123.form.shared.FormContainer
import org.scalajs.dom.window


object FormExporter {

  def exportForm(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createForm).toString())
    tab.focus()
  }

  def createForm: FormContainer =
    UIFormStore.uiState.form.value.toForm
}
