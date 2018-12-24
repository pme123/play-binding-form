package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.I18n
import pme123.form.shared.DataType.STRING
import pme123.form.shared.ElementType.{DROPDOWN, TEXTFIELD}
import pme123.form.shared.services.Language.{DE, EN}
import pme123.form.shared._
import pme123.form.client.services.UIStore.supportedLangs

import scala.util.matching.Regex

private[client] object DataView
  extends MainView {

  val hashRegex: Regex = """#data""".r

  def name: String = "data"

  val link: String = name

  val persistData = Var(false)

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      <div class="ui form">
        {persistDataDiv.bind}{//
        dataContent.bind //
        }
      </div>
    </div>

  // 2. level of abstraction
  // **************************
  @dom
  private lazy val dataContent: Binding[HTMLElement] = {
    val ds = UIDataStore.uiState.dataStructure.bind
    <div class="ui one column cards">
      {dataCard(ds, "").bind}
    </div>
  }

  @dom
  private def dataCard(data: DataStructure, path: String): Binding[HTMLElement] = {
    val structureType = Var(data.structureType)
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
            s"ds-type-$path",
            DROPDOWN,
            DataType.STRING,
            ElementTexts.label(Map(DE -> "Struktur Typ", EN -> "Structure Type")),
            elemEntries = ElementEntries(
              StructureType.values.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
            ),
            value = Some(data.structureType.entryName)
          ),
            changeEvent = Some((str: String) => structureType.value = StructureType.withNameInsensitive(str)),
            extras = Map.empty
          )
        ).bind}</div> <div class="five wide column">{
        val strType = structureType.bind
        strType match {
          case StructureType.STRING =>
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
                changeEvent = Some((str: String) => structureType.value = StructureType.withNameInsensitive(str)),
                extras = Map.empty
              )
            ).bind

          case other => <span>not implemented</span>
        }

        }
      </div>
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
        UIDataStore.uiState.dataStructure.value
      ).bind}
      </div>
    else
        <span/>
  }

}
