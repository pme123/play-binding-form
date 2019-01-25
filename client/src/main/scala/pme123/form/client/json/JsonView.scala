package pme123.form.client.json

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.denigma.codemirror.CodeMirror
import org.denigma.codemirror.extensions.EditorConfig
import org.scalajs.dom._
import org.scalajs.dom.raw.{Event, HTMLElement, HTMLTextAreaElement}
import org.scalajs.jquery.jQuery
import play.api.libs.json.Json
import pme123.form.client._
import pme123.form.client.data.UIDataStore
import pme123.form.client.form.{FormServices, FormUtils, UIFormElem}
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{TEXTAREA, TEXTFIELD}
import pme123.form.shared.ExtraProp.ROWS
import pme123.form.shared._

import scala.scalajs.js.timers.setTimeout

private[client] object JsonView
  extends MainView {

  val link = "json"
  val icon = "file outline"
  val urlFieldId = "url-to-import"
  val jsonFieldId = "json-to-import"

  val callService = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {//
      header(
        Var(""),
        None,
        importButton,
      ).bind}{//
      callServiceDiv.bind}{//
      content.bind}<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val importButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular icon button"
              data:data-tooltip="Import JSON"
              onclick={_: Event =>
                val url = jQuery(s"#$urlFieldId").value().toString

                  if (url.trim.nonEmpty) {
                    callService.value = true
                  } else {
                    val jsonStr = jQuery(s"#$jsonFieldId").value().toString
                    val structure = DataStructure.fromJson(DataStructure.defaultKey, Json.parse(jsonStr))
                    UIDataStore.changeData(structure.asInstanceOf[DataObject])
                  }
                }>
        <i class=" upload icon"></i>
      </button>
    </div>
  }


  @dom
  private lazy val content: Binding[HTMLElement] = {
    <div class="ui card">
      {val url = UIJsonStore.uiState.urlVar.bind
    BaseElementDiv(
      UIFormElem(BaseElement(
        urlFieldId,
        TEXTFIELD,
        ElementTexts.placeholder(I18n("json.import")),
        extras = ExtraProperties(),
        value = url
      ), Some { str =>
        UIJsonStore.uiState.urlVar.value = Some(str)
      }
      )).bind}{//
      val json = UIJsonStore.uiState.jsonVar.bind
      initCodeField()
      BaseElementDiv(
        UIFormElem(BaseElement(
          jsonFieldId,
          TEXTAREA,
          ElementTexts.placeholder(I18n("json.import")),
          extras = ExtraProperties(Seq(
            ExtraPropValue(
              ROWS, "50"
            ))),
          value = json
        ), Some { str =>
          UIJsonStore.uiState.jsonVar.value = Some(str)
        }
        )).bind}
    </div>
  }

  def initCodeField() = {
    setTimeout(500) {
      val params =
        EditorConfig
          .mode("javascript")
          .lineNumbers(true)

      val element = document.getElementById(jsonFieldId)
      // println("element: " + element.id)
      //config
      element match {
        case el: HTMLTextAreaElement =>
          val m = CodeMirror.fromTextArea(el, params)
          m.setSize("100%", "100%")
        case _ => console.error("cannot find text area for the code!")
      }
    }
  }

  @dom
  private lazy val callServiceDiv: Binding[HTMLElement] = {
    val doCall = callService.bind
    if (doCall)
      <div>
        {callService.value = false
      JsonServices.getJson(
        UIJsonStore.uiState.urlVar.value.get
      ).bind}
      </div>
    else
        <span/>
  }
}
