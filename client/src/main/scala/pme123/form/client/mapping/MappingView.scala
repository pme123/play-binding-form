package pme123.form.client.mapping

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client._
import pme123.form.client.data.{DataView, UIDataStore}
import pme123.form.client.form.{FormPreviewView, FormUtils, UIFormElem, UIFormStore}
import pme123.form.client.mapping.UIMappingStore.UIMappingEntry
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.ElementType.DROPDOWN
import pme123.form.shared.ExtraProp.CLEARABLE
import pme123.form.shared._
import pme123.form.shared.services.Language.{DE, EN}
import com.softwaremill.quicklens._

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
      mappingContent.bind}{//
      initFields.bind //
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
              onclick={_: Event =>
                persistMapping.value = true}>
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
    val uiElem = uiMapping.uiFormElem.bind
    val varDataValue = uiMapping.varDataValue.bind
    uiElem.valueVar.value = varDataValue.map(_.content.value)
    val baseElem = uiElem.toBaseElement
      .modify(_.readOnly)
      .setTo(true)
    val layoutWide = uiElem.layoutWideVar.bind
    val wideClass: String = layoutWide.entryName.toLowerCase
    <div class={s"$wideClass wide column"}>
      {BaseElementDiv(UIFormElem(baseElem)).bind}<div>
      &nbsp;
    </div>{//
      val uIFormElem = UIFormElem(BaseElement(
        dataDropdownIdent(uiElem),
        DROPDOWN,
        DataType.STRING,
        ElementTexts.placeholder(Map(DE -> "Gemappt zu ..", EN -> "Maps to ..")),
        elemEntries = ElementEntries(
          UIDataStore.dataValueIdents().map(ident => ElementEntry(ident))
        ),
        value = varDataValue.map(_.ident),
        extras = ExtraProperties(DROPDOWN),
      ),
        Some(UIMappingStore.changeData(uiMappingVar) _)
      )

      uIFormElem.extrasVar.value.valueFor(CLEARABLE).value = "true"
      BaseElementDiv(
        uIFormElem
      ).bind}
    </div>
  }

  private def dataDropdownIdent(uiElem: UIFormElem) = {
    s"mapping-data-${uiElem.identVar.value}"
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

  @dom
  private lazy val initFields = {
    UIRoute.route.state.watch()
    val activeLang = UIStore.uiState.activeLanguage.bind
    val mappings = UIMappingStore.uiState.mapping.value.mappings.value
    val elems = UIFormStore.uiState.formElements.bind
    val fieldRules = FormUtils.semanticFields(elems)(activeLang)
    val dataRules = mappings
      .map { mV =>
        val elemId = dataDropdownIdent(mV.value.uiFormElem.value)
        elemId -> SemanticField(elemId, optional = false, Seq(SemanticUI.emptyRule(activeLang)))
      }.toMap

    SemanticUI.initForm(SemanticForm(fields = fieldRules ++ dataRules))
      <span/>
  }

}
