package pme123.form.client.mock

import org.denigma.codemirror.CodeMirror
import org.denigma.codemirror.extensions.EditorConfig
import org.scalajs.dom.raw.HTMLTextAreaElement
import org.scalajs.dom.{console, document, window}
import play.api.libs.json.Json

import scala.scalajs.js.timers.{SetTimeoutHandle, setTimeout}

object MockUtils {

  def exportMock(): Unit = {
    val tab = window.open("Json Export", "_blank")
    tab.document.write(Json.toJson(UIMockStore.toMock).toString())
    tab.focus()
  }

  def initCodeField(elemId: String, event: String => Unit): SetTimeoutHandle = {
    setTimeout(100) {
      val params =
        EditorConfig
          .mode("javascript")

      //.lineNumbers(true)
      val element = document.getElementById(elemId)
      element match {
        case el: HTMLTextAreaElement =>
          val cm = CodeMirror.fromTextArea(el, params)
          cm.setSize("100%", "100%")
          cm.on("blur", { editor =>
            println("editor.getInputField().textContent: "+ editor.getDoc().getValue("\n"))
            event(editor.getDoc().getValue("\n"))
          })
        case _ => console.error("cannot find text area for the code!")
      }
    }
  }

}
