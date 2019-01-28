package pme123.form.client.mock

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import play.api.libs.json.Json
import pme123.form.client._
import pme123.form.client.data.UIDataStore
import pme123.form.client.form.UIFormElem
import pme123.form.client.mock.UIMockStore.VarMockEntry
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{TEXTAREA, TEXTFIELD}
import pme123.form.shared.ExtraProp.{INPUT_TYPE, ROWS, SIZE}
import pme123.form.shared._

private[client] object MockView
  extends MockyView {

  val link = "mock"
  val icon = "file outline"

  val getJsonFlag = Var(false)
  val persistMockFlag = Var(false)

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
        exportButton,
        persistButton,
        addMockEntryButton,
      ).bind}{//
      getJsonDiv.bind}{//
      persistMockDiv.bind}{//
      content.bind}<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val exportButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular icon button"
              data:data-tooltip="Export Mock Konfiguration"
              onclick={_: Event =>
                MockUtils.exportMock()}>
        <i class="sign-out icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val persistButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular icon button"
              data:data-tooltip="Persist Mock Konfigurations"
              onclick={_: Event =>
                persistMockFlag.value = true}>
        <i class="save outline icon"></i>
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
    val id = varMockEntry.id

    <div class={s"ui ${selectedClass(varMockEntry, selectedEntry)} fluid card"}>
      <div class="extra content">
        <div class="ui grid">
          <div class="eleven wide column">
            {val url = varMockEntry.varUrl.bind
          BaseElementDiv(
            UIFormElem(BaseElement(
              s"url-$id",
              TEXTFIELD,
              ElementTexts.placeholder(I18n("json.import")),
              extras = ExtraProperties(),
              value = Some(url),
              required = true,
            ), Some { str =>
              varMockEntry.varUrl.value = str
            }
            )).bind}
          </div> <div class="two wide column">
          {//
          val status = varMockEntry.varStatus.bind
          BaseElementDiv(
            UIFormElem(BaseElement(
              s"status-$id",
              TEXTFIELD,
              ElementTexts.placeholder(I18n("mock.entry.status")),
              extras = ExtraProperties(Seq(ExtraPropValue(SIZE, "3"), ExtraPropValue(INPUT_TYPE, InputType.NUMBER.key))),
              value = Some(status.toString),
              required = true,
              validations = Validations(Seq(ValidationRule(ValidationType.INTEGER, enabled = true,
                ValidationParams(intParam1 = Some(100), intParam2 = Some(999))))),
            ), Some { str =>
              varMockEntry.varStatus.value = str.toInt
            }
            )).bind}
        </div> <div class="three wide column">
          <button class="mini ui circular icon button"
                  data:data-tooltip="Import JSON"
                  onclick={_: Event =>
                    val url = varMockEntry.varUrl.value
                    if (url.trim.nonEmpty) {
                      getJsonFlag.value = true
                    } else {
                      val structure = DataStructure.fromJson(DataStructure.defaultKey, Json.parse(varMockEntry.varContent.value))
                      UIDataStore.changeData(structure.asInstanceOf[DataObject])
                    }}>
            <i class=" upload icon"></i>
          </button>
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
        </div>
      </div>{//
      if (varMockEntry == UIMockStore.uiState.varSelectedEntry.value) {
        val content = varMockEntry.varContent.bind
        val elemId = s"content-$id"
        println(s"created: $content")
        MockUtils.initCodeField(elemId, str => varMockEntry.varContent.value = str)
        BaseElementDiv(
          UIFormElem(BaseElement(
            elemId,
            TEXTAREA,
            ElementTexts.placeholder(I18n("json.import")),
            extras = ExtraProperties(Seq(
              ExtraPropValue(
                ROWS, "50"
              ))),
            value = Some(content),
            required = true,
          )
          )).bind
      } else {
          <span/>
      }}
    </div>
  }

  @dom
  private lazy val getJsonDiv: Binding[HTMLElement] = {
    val doCall = getJsonFlag.bind
    if (doCall)
      <div>
        {getJsonFlag.value = false
      val mockEntry = UIMockStore.uiState.varSelectedEntry.value
      val ident = UIMockStore.uiState.varIdent.value
      MockServices.callService(ServiceRequest(ident, mockEntry.varUrl.value)).bind}
      </div>
    else
        <span/>
  }

  @dom
  private lazy val persistMockDiv: Binding[HTMLElement] = {
    val doCall = persistMockFlag.bind
    if (doCall)
      <div>
        {persistMockFlag.value = false
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
