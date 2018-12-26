package pme123.form.client

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.shared.{DataContainer, FormContainer}


object DataExporter {

  def exportData(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createData).toString())
    tab.focus()
  }

   def createData: DataContainer = {
     UIDataStore.uiState.data.value.toData
  }
}