package pme123.form.client.form

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.shared.FormContainer

object FormExporter {

  def exportForm(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createForm).toString())
    tab.focus()
  }

  def createForm: FormContainer =
    UIFormStore.uiState.form.value.toForm
}
