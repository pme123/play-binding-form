package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, FormData, HTMLElement, HTMLInputElement}
import org.scalajs.jquery
import play.api.libs.json.{JsBoolean, JsNumber}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{I18n, SemanticUI}
import pme123.form.shared.DataType.STRING
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared._
import pme123.form.shared.services.Language.{DE, EN}

import scala.util.matching.Regex

private[client] object DataView
  extends MainView {

  val hashRegex: Regex = """#data""".r

  def name: String = "data"

  val link: String = name

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
    val ds = UIDataStore.uiState.dataStructure.bind
    <div class="ui one column cards">
      {dataCard(ds, "").bind}
    </div>
  }

  @dom
  private def dataCard(data: DataStructure, path: String): Binding[HTMLElement] = {
    println(s"PATH: $path")
    val structureType = Var(data.structureType)
    val strType = structureType.bind
    SemanticUI.initElements()
    <div class="ui fluid card">
      <div class="ui grid content">
        <div class="six wide column">
          {BaseElementDiv(
          UIFormElem(
            BaseElement(
              s"ds-ident-$path",
              TEXTFIELD,
              STRING,
              ElementTexts.label(Map(EN -> "Ident", DE -> "Ident")),
              value = Some(data.ident),
              required = true,
            ),
          )
        ).bind}
        </div> <div class="five wide column">
        {//
        BaseElementDiv(
          UIFormElem(BaseElement(
            s"ds-type$path",
            DROPDOWN,
            DataType.STRING,
            ElementTexts.label(Map(DE -> "Struktur Typ", EN -> "Structure Type")),
            elemEntries = ElementEntries(
              StructureType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
            ),
            value = Some(data.structureType.entryName)
          ),
            changeEvent = Some((str: String) => structureType.value =
              StructureType.withNameInsensitive(str)),
            extras = Map.empty
          )
        ).bind}
      </div> <div class="five wide column">
        {strType match {
          case StructureType.STRING => {
            BaseElementDiv(
              UIFormElem(
                BaseElement(
                  s"ds-value-$path",
                  TEXTFIELD,
                  STRING,
                  ElementTexts.label(Map(EN -> "String value", DE -> "Text")),
                  value = Some(data.structure.validate[String].getOrElse("")),
                  required = true,
                ),
              )
            ).bind
          }
          case StructureType.NUMBER => {
            BaseElementDiv(
              UIFormElem(
                BaseElement(
                  s"ds-value-$path",
                  TEXTFIELD,
                  STRING,
                  ElementTexts.label(Map(EN -> "Number value", DE -> "Nummer")),
                  value = Some(data.structure.validate[JsNumber].map(_.value.toString).getOrElse("0")),
                  required = true,
                ),
              )
            ).bind
          }
          case StructureType.BOOLEAN => {
            BaseElementDiv(
              UIFormElem(
                BaseElement(
                  s"ds-value-$path",
                  CHECKBOX,
                  STRING,
                  ElementTexts.label(Map(EN -> "Boolean value", DE -> "Ja / Nein")),
                  value = Some(data.structure.validate[JsBoolean].map(_.value.toString).getOrElse("false")),
                ),
              )
            ).bind
          }
          case StructureType.OBJECT =>

            objectButtons().bind
          case other => <span>not implemented</span>
        }}
      </div>{//
        if (strType == StructureType.OBJECT)
          nextDataStructure(data, path).bind
        else
            <span/>}
      </div>
    </div>
  }

  @dom
  private def objectButtons(): Binding[HTMLElement] = {
    <div class="five wide column">
      {"buttons"}
    </div>
  }

  @dom
  private def nextDataStructure(data: DataStructure, path: String): Binding[HTMLElement] = {
    <div class="sixteen wide column">
      {dataCard(
      data.structure.validate[DataStructure].getOrElse(DataStructure()),
      s"$path--${data.ident}"
    ).bind}
    </div>
  }

  @dom
  private lazy val persistDataDiv: Binding[HTMLElement] = {
    val doPersist = persistData.bind
    if (doPersist)
      <div>
        {persistData.value = false
      DataServices.persistData(
        UIDataStore.uiState.dataStructure.value
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
