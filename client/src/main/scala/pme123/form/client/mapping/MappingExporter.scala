package pme123.form.client.mapping

import org.scalajs.dom.window
import play.api.libs.json.Json
import pme123.form.shared.MappingContainer

object MappingExporter {

  def exportMapping(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(createMapping).toString())
    tab.focus()
  }

   def createMapping: MappingContainer = {
     UIMappingStore.uiState.mapping.value.toMapping
  }
}
