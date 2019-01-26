package pme123.form.client.mock

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
import pme123.form.client.form.UIFormElem
import pme123.form.client.mock.UIMockStore.VarMockEntry
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{TEXTAREA, TEXTFIELD}
import pme123.form.shared.ExtraProp.{ROWS, SIZE}
import pme123.form.shared._

import scala.scalajs.js.timers.setTimeout

private[client] object MockView
  extends MainView {

  val link = "json"
  val icon = "file outline"
  val urlFieldId = "url-to-import"
  val jsonFieldId = "json-to-import"

  val getJsonFlag = Var(false)
  val submitMockEntryFlag = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {//
      header(
        UIMockStore.uiState.varIdent,
        Some(UIMockStore.changeMockIdent),
        importButton,
        createMockButton,
        addMockEntryButton,
      ).bind}{//
      getJsonDiv.bind}{//
      submitMockEntryDiv.bind}{//
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
                  getJsonFlag.value = true
                } else {
                  val jsonStr = jQuery(s"#$jsonFieldId").value().toString
                  val structure = DataStructure.fromJson(DataStructure.defaultKey, Json.parse(jsonStr))
                  UIDataStore.changeData(structure.asInstanceOf[DataObject])
                }}>
        <i class=" upload icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val createMockButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular icon button"
              data:data-tooltip="Submit Mock Entry"
              onclick={_: Event =>
                submitMockEntryFlag.value = true}>
        <i class=" thumbtack icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val addMockEntryButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular blue icon button"
              data:data-tooltip="Add Form Element"
              onclick={_: Event =>
                UIMockStore.addMockEntry()}>
        <i class="add icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val content: Binding[HTMLElement] = {
    <div class="ui container">
      <div class="ui cards">
        {for (entry <- UIMockStore.uiState.varsMockEntries)
        yield element(entry).bind //
        }
      </div>
    </div>
  }

  @dom
  private def element(varMockEntry: VarMockEntry): Binding[HTMLElement] = {
    val selectedEntry = UIMockStore.uiState.varSelectedEntry.bind
    val url = varMockEntry.varUrl.bind
    <div class={s"ui ${selectedClass(varMockEntry, selectedEntry)} fluid card"}>
      <div class="extra content">
        <div class="left floated">
          {BaseElementDiv(
          UIFormElem(BaseElement(
            urlFieldId,
            TEXTFIELD,
            ElementTexts.placeholder(I18n("json.import")),
            extras = ExtraProperties(Seq(ExtraPropValue(SIZE, "100"))),
            value = Some(url),
            required = true,
          ), Some { str =>
            varMockEntry.varUrl.value = str
          }
          )).bind}
        </div>
        <div class="right floated">

          <button class="mini ui circular blue icon button"
                  data:data-tooltip="Edit Form Element"
                  onclick={_: Event =>
                    UIMockStore.changeSelectedEntry(varMockEntry)}>
            <i class="edit icon"></i>
          </button>
          <button class="mini ui circular grey icon button"
                  data:data-tooltip="Copy Form Element"
                  onclick={_: Event =>
                    UIMockStore.copySelectedEntry(varMockEntry)}>
            <i class="copy icon"></i>
          </button>
          <button class="mini ui circular red icon button"
                  data:data-tooltip="Delete Mock Entry"
                  onclick={_: Event =>
                    UIMockStore.deleteSelectedEntry(varMockEntry)}>
            <i class="trash icon"></i>
          </button>
        </div>
      </div>{//
      if (varMockEntry == UIMockStore.uiState.varSelectedEntry.value) {
        val content = varMockEntry.varContent.bind
        val id = MockServices.urlAsBase64(url)
        initCodeField(id)
        BaseElementDiv(
          UIFormElem(BaseElement(
            id,
            TEXTAREA,
            ElementTexts.placeholder(I18n("json.import")),
            extras = ExtraProperties(Seq(
              ExtraPropValue(
                ROWS, "50"
              ))),
            value = Some(content),
            required = true,
          ), Some { str =>
            varMockEntry.varContent.value = str
          }
          )).bind
      } else {
          <span/>
      }}
    </div>
  }

  private def initCodeField(elemId: String) = {
    setTimeout(100) {
      val params =
        EditorConfig
          .mode("javascript")
      //.lineNumbers(true)
      val element = document.getElementById(elemId)
      element match {
        case el: HTMLTextAreaElement =>
          val m = CodeMirror.fromTextArea(el, params)
          m.setSize("100%", "100%")
        case _ => console.error("cannot find text area for the code!")
      }
    }
  }

  @dom
  private lazy val getJsonDiv: Binding[HTMLElement] = {
    val doCall = getJsonFlag.bind
    if (doCall)
      <div>
        {getJsonFlag.value = false
      val url = UIMockStore.uiState.varSelectedEntry.value.varUrl.value
      MockServices.callService(url).bind}
      </div>
    else
        <span/>
  }

  @dom
  private lazy val submitMockEntryDiv: Binding[HTMLElement] = {
    val doCall = submitMockEntryFlag.bind
    if (doCall)
      <div>
        {submitMockEntryFlag.value = false
      MockServices.persistMock(UIMockStore.toMock).bind}
      </div>
    else
        <span/>
  }

  private def selectedClass(varEntry: VarMockEntry, varSelectedEntry: VarMockEntry) =
    if (varEntry == varSelectedEntry)
      "blue"
    else
      ""
}
