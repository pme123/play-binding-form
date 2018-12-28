package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, FormData, HTMLElement, HTMLInputElement}
import pme123.form.client.UIDataStore._
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.DataType.STRING
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared._
import pme123.form.shared.services.Language.{DE, EN}

private[client] object DataView
  extends MainView {

  val link: String = "data"
  val icon = "database"

  val persistData = Var(false)

  val submitForm: Var[Option[FormData]] = Var(None)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {//
      header(
        UIDataStore.uiState.data.value.identVar,
        Some(UIFormStore.changeIdent),
        uploadButton,
        headerButtons).bind //
      }<div class="ui form">
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
  private lazy val uploadButton: Binding[HTMLElement] = {
    <form id="dataHeaderForm">
      <input
      id="importDataStructure"
      name="importDataStructure"
      type="file"
      class="inputfile"
      onchange={_: Event =>
        val aIn: HTMLInputElement = importDataStructure.asInstanceOf[HTMLInputElement]
        if (aIn.files.length > 0)
          submitForm.value = Some(new FormData(dataHeaderForm))}/>

      <label for="importDataStructure"
             data:data-tooltip="Import Data from JSON"
             class="ui circular button">
        <i class="ui upload icon"></i>
      </label>
    </form>
  }
  @dom
  private lazy val headerButtons: Binding[HTMLElement] = {
    <div class="">
      &nbsp;
      &nbsp;
      <button class="ui circular icon button"
              data:data-tooltip="Export Data as JSON"
              onclick={_: Event =>
                DataExporter.exportData()}>
        <i class="sign-out icon"></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular blue icon button"
              data:data-tooltip="Persist Data on Server"
              onclick={_: Event =>
                persistData.value = true}>
        <i class="save outline icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val dataContent: Binding[HTMLElement] = {
    val ds = UIDataStore.uiState.data.bind
    <div class="ui one column cards">
      {dataObjectContent(ds.identVar.value, ds.structure, None).bind}
    </div>
  }

  @dom
  private def dataObjectContent(ident: String, data: Var[VarDataObject], parent: Option[Var[VarDataObject]]): Binding[HTMLElement] = {
    <div class="ui fluid card">
      {//
      val obj = data.bind
      Constants(obj.content.map { case (i, content) => dataStructureContent(i, content, data) }.toSeq: _*).map(_.bind)}<div class="extra content">
      {objectButtons(ident, data, parent).bind}
    </div>
    </div>
  }

  @dom
  private def dataStructureContent(ident: String, data: Var[VarDataStructure], parent: Var[VarDataObject]): Binding[HTMLElement] = {
    val _ = data.bind
    <div class="ui grid content">
      {identDiv(ident, parent).bind}{//
      structureTypeDiv(ident, data).bind}{//
      dataContent(ident, data, parent).bind}
    </div>
  }

  private def dataContent(ident: String, data: Var[VarDataStructure], parent: Var[VarDataObject]) = {
    data.value match {
      case VarDataString(content) =>
        dataStringContent(ident, content)
      case VarDataNumber(content) =>
        dataNumberContent(ident, content)
      case VarDataBoolean(content) =>
        dataBooleanContent(ident, content)
      case _: VarDataObject =>
        dataObjectContent(ident, data.asInstanceOf[Var[VarDataObject]], Some(parent))
    }
  }

  @dom
  private def dataStringContent(ident: String, data: Var[String]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "String value", DE -> "Text")),
          value = Some(data.value),
          required = true,
        ),
        changeEvent = Some(
          str => data.value = str
        ),
        extras = Map.empty
      )
    ).bind}
    </div>
  }

  @dom
  private def dataNumberContent(ident: String, data: Var[BigDecimal]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "Number value", DE -> "Nummer")),
          value = Some(data.value.toString),
          required = true,
        ),
        changeEvent = Some(
          str => data.value = BigDecimal(str)
        ),
        extras = Map.empty
      )
    ).bind}
    </div>
  }

  @dom
  private def dataBooleanContent(ident: String, data: Var[Boolean]): Binding[HTMLElement] = {
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          CHECKBOX,
          STRING,
          ElementTexts.label(Map(EN -> "Boolean value", DE -> "Ja / Nein")),
          value = Some(data.value.toString),
        ),
        changeEvent = Some(
          str => data.value = str.toBoolean
        ),
        extras = Map.empty
      )
    ).bind}
    </div>
  }

  @dom
  private def identDiv(ident: String, parent: Var[VarDataObject]): Binding[HTMLElement] = {
    <div class="six wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-ident-$ident",
          TEXTFIELD,
          STRING,
          ElementTexts.label(Map(EN -> "Ident", DE -> "Ident")),
          value = Some(ident),
          required = true,
        ), changeEvent = Some(
          UIDataStore.changeIdent(parent, ident)
        ),
        extras = Map.empty
      )
    ).bind}
    </div>
  }

  @dom
  private def structureTypeDiv(ident: String, data: Var[VarDataStructure]): Binding[HTMLElement] = {
    <div class="five wide column">
      {//
      BaseElementDiv(
        UIFormElem(BaseElement(
          s"ds-type-$ident",
          DROPDOWN,
          DataType.STRING,
          ElementTexts.label(Map(DE -> "Struktur Typ", EN -> "Structure Type")),
          elemEntries = ElementEntries(
            StructureType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
          ),
          value = Some(data.value.structureType.entryName)
        ),
          changeEvent = Some(
            UIDataStore.changeStructureType(data)
          ),
          extras = Map.empty
        )
      ).bind}
    </div>
  }

  @dom
  private def objectButtons(ident: String, data: Var[VarDataObject], parent: Option[Var[VarDataObject]]): Binding[HTMLElement] = {
    <div class="five wide column">
      <div class="right floated">

        <button class="mini ui circular blue icon button"
                data:data-tooltip="Add Data Object"
                onclick={_: Event =>
                  UIDataStore.addDataObject(data)}>
          <i class="add icon"></i>
        </button>{//
        if (parent.nonEmpty)
          <button class="mini ui circular red icon button"
                  data:data-tooltip="Delete Data Object"
                  onclick={_: Event =>
                    UIDataStore.deleteDataObject(ident, parent.get)}>
            <i class="trash icon"></i>
          </button>
        else
            <span/>}
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
