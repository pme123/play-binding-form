package pme123.form.client.data

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.client.data.UIDataStore.VarDataObject
import pme123.form.client.services.SemanticUI
import pme123.form.shared.{DataObject, FormContainer}

object DataUtils {

  def exportData(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createData).toString())
    tab.focus()
  }

  def createData(form: FormContainer): Unit = {
    UIDataStore.uiState.identVar.value = s"${form.ident}-data"
    UIDataStore.uiState.data.value = VarDataObject(UIDataStore.uiState.identVar, form)
    SemanticUI.initElements()
  }

  def createData: DataObject = {
    UIDataStore.uiState.data.value.toData
  }
}
