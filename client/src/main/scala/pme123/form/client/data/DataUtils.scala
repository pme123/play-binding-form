package pme123.form.client.data

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.client.data.UIDataStore.VarDataContainer
import pme123.form.client.services.SemanticUI
import pme123.form.shared.{DataContainer, FormContainer}

object DataUtils {

  def exportData(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createData).toString())
    tab.focus()
  }

  def createData(form: FormContainer): Unit = {
    println("Create DATA")
    UIDataStore.uiState.identVar.value = s"${form.ident}-data"
    UIDataStore.uiState.data.value = VarDataContainer(UIDataStore.uiState.identVar, form)
    SemanticUI.initElements()
  }

  def createData: DataContainer = {
    UIDataStore.uiState.data.value.toData
  }
}
