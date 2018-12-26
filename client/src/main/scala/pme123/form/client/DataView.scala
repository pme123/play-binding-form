package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, FormData, HTMLElement, HTMLInputElement}
import pme123.form.client.UIDataStore._
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{I18n, SemanticUI}
import pme123.form.shared.DataType.STRING
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared._
import pme123.form.shared.services.Language.{DE, EN}

private[client] object DataView
  extends MainView {

  val link: String = "data"

  val persistData = Var(false)

  val submitForm: Var[Option[FormData]] = Var(None)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {dataHeader.bind}<div class="ui form">
      {//
      submitFormDiv.bind}{//
      persistDataDiv.bind}{//
      dataContent.bind //
      }
    </div>
    </div>

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val dataHeader: Binding[HTMLElement] = {
    <div class="ui borderless datamenu menu">
      <div class="ui item">
        <h3 class="header">
          <i class="edit outline icon"></i> &nbsp; &nbsp;
          Data Editor</h3>
      </div>
      <div class="ui right item">
        <form id="dataHeaderForm">
          <input
          id="importDataStructure"
          name="importDataStructure"
          type="file"
          class="inputfile"
          onchange={ev: Event =>
            val aIn: HTMLInputElement = importDataStructure.asInstanceOf[HTMLInputElement]
            if (aIn.files.length > 0)
              submitForm.value = Some(new FormData(dataHeaderForm))}/>

          <label for="importDataStructure"
                 class="ui right floated button">
            <i class="ui upload icon"></i>
            Import Data Structure
          </label>
        </form>

      </div>
    </div>
  }

  @dom
  private lazy val dataContent: Binding[HTMLElement] = {
    val ds = UIDataStore.uiState.data.bind
    <div class="ui one column cards">
      {dataObjectContent(ds.structure).bind}
    </div>
  }

  @dom
  private def dataObjectContent(data: VarDataObject): Binding[HTMLElement] = {
    <div class="ui fluid card">
        {//
        val childMap = data.content.bind
        Constants(childMap.map { case (i, content) => dataStructureContent(i, content) }.toSeq: _*).map(_.bind)}
      <div class="extra content">{
        objectButtons(data).bind
        }
      </div>
    </div>
  }

  @dom
  private def dataStructureContent(ident: String, data: VarDataStructure): Binding[HTMLElement] = {
   // val elem: Binding[HTMLElement] = dataContent(ident, data)
    <div class="ui grid content">
      {identDiv(ident).bind}{//
      structureTypeDiv(data.structureType).bind}{//
      dataContent(ident, data).bind
    }
    </div>
  }

  private def dataContent(ident: String, data: VarDataStructure) = {
    data match {
      case VarDataString(content) =>
        dataStringContent(content)
      case VarDataNumber(content) =>
        dataNumberContent(content)
      case VarDataBoolean(content) =>
        dataBooleanContent(content)
      case dataObj: VarDataObject =>
        dataObjectContent(dataObj)
    }
  }

  @dom
  private def dataStringContent(data: Var[String]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-TODO",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "String value", DE -> "Text")),
          value = Some(data.value),
          required = true,
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def dataNumberContent(data: Var[BigDecimal]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-TODO",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "Number value", DE -> "Nummer")),
          value = Some(data.value.toString),
          required = true,
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def dataBooleanContent(data: Var[Boolean]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-TODO",
          CHECKBOX,
          STRING,
          ElementTexts.label(Map(EN -> "Boolean value", DE -> "Ja / Nein")),
          value = Some(data.value.toString),
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def identDiv(ident: String): Binding[HTMLElement] = {
    <div class="six wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-ident-TODO",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "Ident", DE -> "Ident")),
          value = Some(ident),
          required = true,
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def structureTypeDiv(structureType: StructureType): Binding[HTMLElement] = {
    <div class="five wide column">
      {//
      BaseElementDiv(
        UIFormElem(BaseElement(
          s"ds-type-TODO",
          DROPDOWN,
          DataType.STRING,
          ElementTexts.label(Map(DE -> "Struktur Typ", EN -> "Structure Type")),
          elemEntries = ElementEntries(
            StructureType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
          ),
          value = Some(structureType.entryName)
        ),
          changeEvent = Some((str: String) => () /*structureType.value =
            StructureType.withNameInsensitive(str)*/),
          extras = Map.empty
        )
      ).bind}
    </div>
  }

  @dom
  private def objectButtons(data: VarDataStructure): Binding[HTMLElement] = {
    <div class="five wide column">
      <div class="right floated">

        <button class="mini ui circular blue icon button"
                data:data-tooltip="Add Data Object"
                onclick={_: Event =>
                  UIDataStore.addDataObject(data)}>
          <i class="add icon"></i>
        </button>
        <button class="mini ui circular red icon button"
                data:data-tooltip="Delete Data Object"
                onclick={_: Event =>
                //  UIFormStore.deleteDataObject(path)
                }>
          <i class="trash icon"></i>
        </button>
      </div>
    </div>
  }

  @dom
  private lazy val persistDataDiv: Binding[HTMLElement] = {
    val doPersist = persistData.bind
    if (doPersist)
      <div>
        {persistData.value = false
      DataServices.persistData(
        UIDataStore.uiState.data.value.toData
      ).bind}
      </div>
    else
        <span/>
  }

  @dom
  private lazy val submitFormDiv: Binding[HTMLElement] = {
    val doSubmit = submitForm.bind
    if (doSubmit.nonEmpty)
      <div>
        {submitForm.value = None
      DataServices.submitForm(doSubmit.get).bind}
      </div>
    else
        <span/>
  }

}
