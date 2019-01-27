package pme123.form.client.mock

import org.scalajs.dom.window
import play.api.libs.json.Json

object MockUtils {

  def exportMock(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(UIMockStore.toMock).toString())
    tab.focus()
  }

}
