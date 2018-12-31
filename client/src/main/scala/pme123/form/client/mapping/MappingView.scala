package pme123.form.client.mapping

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client._
import pme123.form.client.data.{DataView, UIDataStore}
import pme123.form.client.mapping.UIMappingStore.UIMappingEntry
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.DROPDOWN
import pme123.form.shared._

private[client] object MappingView
  extends MainView {

  val link = "mapping"
  val icon = "sync"

  val persistMapping = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {persistMappingDiv.bind}{//
      header(
        UIMappingStore.uiState.mapping.value.identVar,
        Some(UIMappingStore.changeMappingIdent),
        formDataIdent,
        validateButton,
        exportButton,
        persistButton).bind}{//
      mappingContent.bind //
      }<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************
  @dom
  private def formDataIdent: Binding[HTMLElement] = {
    <div class="">
      <div class="compact vertical menu">
        <div class="">
          <i class={s"icon ${FormPreviewView.icon}"}></i>{UIFormStore.uiState.identVar.bind}
        </div>
        <div class="">
          &nbsp;
        </div>
        <div class="">
          <i class={s"icon ${DataView.icon}"}></i>{UIDataStore.uiState.identVar.bind}
        </div>
      </div>
    </div>
  }

  @dom
  private lazy val validateButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular show-valid icon submit button"
              data:data-tooltip="Validate Mapping"
              onclick={_: Event =>}>
        <i class="check icon"></i>
      </button>
    </div>
  }
  @dom
  private lazy val exportButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular icon button"
              data:data-tooltip="Export Mapping as JSON"
              onclick={_: Event =>
                MappingExporter.exportMapping()}>
        <i class="sign-out icon"></i>
      </button>
    </div>
  }
  @dom
  private lazy val persistButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular blue icon button"
              data:data-tooltip="Persist Mapping on Server"
              onclick={_: Event =>
                persistMapping.value = true}>
        <i class="save outline icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val mappingContent: Binding[HTMLElement] = {
    val mapping = UIMappingStore.uiState.mapping.bind
    val elems = mapping.form.value.elems.bind
    val newMappings = UIMappingStore.adjustMappings(elems)
    <div class="ui grid">
      {for (elem <- newMappings) yield element(elem).bind //
      }
    </div>
  }

  @dom
  private def element(uiMappingVar: Var[UIMappingEntry]): Binding[HTMLElement] = {
    val uiMapping = uiMappingVar.bind
    val uiElem = uiMapping.uiFormElem.value
    val varDataValue = uiMapping.varDataValue.bind
    println("dataIdent: " + varDataValue.map(_.ident))
    println("dataValue: " + varDataValue.map(_.content))

    <div class={s"${uiElem.wideClass} wide column"}>
      {TextFieldDiv
      .create(uiElem
        .modify(_.elem.readOnly).setTo(true)
        .modify(_.elem.value).setTo(varDataValue.map(_.content.value))
      ).bind}<div class="field">
      {BaseElementDiv(
        UIFormElem(BaseElement(
          s"mapping-data-${uiElem.elem.ident}",
          DROPDOWN,
          DataType.STRING,
          ElementTexts(),
          elemEntries = ElementEntries(
            UIDataStore.dataValueIdents().map(ident => ElementEntry(ident))
          ),
          value = varDataValue.map(_.ident)
        ),
          Some(UIMappingStore.changeData(uiMappingVar) _)
        )
      ).bind}
    </div>
    </div>
  }

  @dom
  private lazy val persistMappingDiv: Binding[HTMLElement] = {
    val doPersist = persistMapping.bind
    if (doPersist)
      <div>
        {persistMapping.value = false
      MappingServices.persistMapping(
        MappingExporter.createMapping
      ).bind}
      </div>
    else
        <span/>
  }

}
