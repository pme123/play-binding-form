package pme123.form.client.data

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, FormData, HTMLElement, HTMLInputElement}
import pme123.form.client._
import pme123.form.client.data.UIDataStore._
import pme123.form.client.form.{FormPreviewView, UIFormElem, UIFormStore}
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
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
        Some(str =>
          UIDataStore.uiState.identVar.value = str),
        uploadButton,
        headerButtons).bind //
      }<div class="ui form data-view">
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
              data:data-tooltip="Create Data from Form"
              onclick={_: Event =>
                DataUtils.createData(UIFormStore.uiState.form.value.toForm)}>
        <i class={s"${FormPreviewView.icon} icon"}></i>
      </button>
      &nbsp;
      &nbsp;
      <button class="ui circular icon button"
              data:data-tooltip="Export Data as JSON"
              onclick={_: Event =>
                DataUtils.exportData()}>
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
    <div class="ui cards">
      <div class="ui fluid card">
        {dataStructureContent(ds.structure, None).bind}
      </div>
    </div>
  }

  @dom
  private def dataObjectContent(data: Var[VarDataObject]): Binding[HTMLElement] = {
    <div class="ui fluid card">
      {//
      val obj = data.bind
      for ((_, content) <- obj.content) yield dataStructureContent(content, Some(obj.content)).bind}
    </div>
  }

  @dom
  private def dataStructureContent(data: Var[_ <: VarDataStructure], parentContent: Option[Vars[(String, Var[_ <: VarDataStructure])]]): Binding[HTMLElement] = {
    val d = data.bind
    <div draggable={parentContent.nonEmpty} class="ui grid content">
      {//
      buttons(data, parentContent).bind}{//
      identDiv(d.identVar).bind}{//
      structureTypeDiv(data).bind}{//
      dataContent(data).bind}
    </div>
  }

  private def dataContent(data: Var[_ <: VarDataStructure]): Binding[HTMLElement] = {
    data.value match {
      case VarDataValue(ident, StructureType.STRING, content) =>
        dataStringContent(ident, content)
      case VarDataValue(ident, StructureType.NUMBER, content) =>
        dataNumberContent(ident, content)
      case VarDataValue(ident, StructureType.BOOLEAN, content) =>
        dataBooleanContent(ident, content)
      case _: VarDataObject =>
        dataObjectContent(data.asInstanceOf[Var[VarDataObject]])
    }
  }

  @dom
  private def dataStringContent(identVar: Var[String], data: Var[String]): Binding[HTMLElement] = {
    val ident = identVar.bind
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          TEXTFIELD,
          ElementTexts.label(Map(EN -> "String value", DE -> "Text")),
          value = Some(data.value),
          extras = ExtraProperties(TEXTFIELD),
          required = true,
        ),
        changeEvent = Some(
          str => data.value = str
        )
      )
    ).bind}
    </div>
  }

  @dom
  private def dataNumberContent(identVar: Var[String], data: Var[String]): Binding[HTMLElement] = {
    val ident = identVar.bind
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          TEXTFIELD,
          ElementTexts.label(Map(EN -> "Number value", DE -> "Nummer")),
          value = Some(data.value.toString),
          extras = ExtraProperties(TEXTFIELD),
          required = true,
        ),
        changeEvent = Some(
          str => data.value = str
        ))
    ).bind}
    </div>
  }

  @dom
  private def dataBooleanContent(identVar: Var[String], data: Var[String]): Binding[HTMLElement] = {
    val ident = identVar.bind
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-value-$ident",
          CHECKBOX,
          ElementTexts.label(Map(EN -> "Boolean value", DE -> "Ja / Nein")),
          value = Some(data.value.toString),
          extras = ExtraProperties(CHECKBOX),
        ),
        changeEvent = Some(
          str => data.value = str
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def identDiv(identVar: Var[String]): Binding[HTMLElement] = {
    val ident = identVar.bind
    <div class="five wide column">
      {BaseElementDiv(
      UIFormElem(
        BaseElement(
          s"ds-ident-$ident",
          TEXTFIELD,
          ElementTexts.label(Map(EN -> "Ident", DE -> "Ident")),
          value = Some(ident),
          extras = ExtraProperties(TEXTFIELD),
          required = true,
        ), changeEvent = Some(str =>
          identVar.value = str
        ),
      )
    ).bind}
    </div>
  }

  @dom
  private def structureTypeDiv(data: Var[_ <: VarDataStructure]): Binding[HTMLElement] = {
    <div class="five wide column">
      {//
      BaseElementDiv(
        UIFormElem(BaseElement(
          s"ds-type-${data.value.identVar.value}",
          DROPDOWN,
          ElementTexts.label(Map(DE -> "Struktur Typ", EN -> "Structure Type")),
          elemEntries = ElementEntries(
            StructureType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
          ),
          extras = ExtraProperties(DROPDOWN),
          value = Some(data.value.structureType.entryName)
        ),
          changeEvent = Some(
            UIDataStore.changeStructureType(data.asInstanceOf[Var[VarDataStructure]])
          ),
        )
      ).bind}
    </div>
  }

  @dom
  private def buttons(data: Var[_ <: VarDataStructure], parentContent: Option[Vars[(String, Var[_ <: VarDataStructure])]]): Binding[HTMLElement] = {
    <div class="one wide column">
      {addButton(data).bind}{//
      deleteButton(data, parentContent).bind}
    </div>
  }

  @dom
  private def addButton(parentVar: Var[_ <: VarDataStructure]): Binding[HTMLElement] = {
    val data = parentVar.bind
    if (data.structureType == StructureType.OBJECT)
      <button class="mini ui circular blue icon button"
              data:data-tooltip="Add Data Object"
              onclick={_: Event =>
                UIDataStore.addDataObject(parentVar.asInstanceOf[Var[VarDataObject]])}>
        <i class="add icon"></i>
      </button> else
        <span/>
  }

  @dom
  private def deleteButton(dataVar: Var[_ <: VarDataStructure], parentContent: Option[Vars[(String, Var[_ <: VarDataStructure])]]): Binding[HTMLElement] = {
    val data = dataVar.bind
    if (parentContent.nonEmpty)
      <button class="mini ui circular red icon button"
              data:data-tooltip="Delete Data Object"
              onclick={_: Event =>
                UIDataStore.deleteDataObject(data.identVar.value, parentContent.get)}>
        <i class="trash icon"></i>
      </button>
    else
        <span/>
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
