package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.shared.MappingEntry

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
      header(UIFormStore.uiState.ident, Some(UIFormStore.changeIdent), headerButtons).bind}{//
      mappingContent.bind //
      }<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************


  @dom
  private lazy val headerButtons: Binding[HTMLElement] = {
    <div class="">
      <button class="ui circular show-valid icon submit button"
              data:data-tooltip="Validate Mapping"
              onclick={_: Event =>
                persistMapping.value = true}>
        <i class="check icon"></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular icon button"
              data:data-tooltip="Export Mapping as JSON"
              onclick={_: Event =>
                MappingExporter.exportMapping()}>
        <i class="sign-out icon"></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular blue icon button"
              data:data-tooltip="Persist Mapping on Server"
              onclick={_: Event =>
                persistMapping.value = true}>
        <i class="save outline icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val mappingContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- UIMappingStore.uiState.mapping.value.mappings) yield element(elem).bind //
      }
    </div>

  @dom
  private def element(uiElemVar: Var[MappingEntry]): Binding[HTMLElement] = {
    val mEntry = uiElemVar.bind
    <div class={s"sixteen wide column"}>
      {mEntry.dataIdent}
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
