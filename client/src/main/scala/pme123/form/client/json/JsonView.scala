package pme123.form.client.json

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import play.api.libs.json.Json
import pme123.form.client._
import pme123.form.client.data.UIDataStore
import pme123.form.client.form.UIFormElem
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.TEXTAREA
import pme123.form.shared.ExtraProp.ROWS
import pme123.form.shared._

private[client] object JsonView
  extends MainView {

  val link = "json"
  val icon = "file outline"

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {//
      header(
        Var(""),
        None,
        importButton,
      ).bind}{//
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
                val str = jQuery("#json-to-import").value().toString
                val structure = DataStructure.fromJson(DataStructure.defaultKey, Json.parse(str))
                UIDataStore.changeData(structure.asInstanceOf[DataObject])}>
        <i class=" upload icon"></i>
      </button>
    </div>
  }


  @dom
  private lazy val content: Binding[HTMLElement] = {
    <div class="ui card">
          {BaseElementDiv(
      UIFormElem(BaseElement(
        s"json-to-import",
        TEXTAREA,
        ElementTexts.placeholder(I18n("json.import")),
        extras = ExtraProperties(Seq(
          ExtraPropValue(
            ROWS, "50"
          ))),
        value = Some(testJson)
      )
      )
    ).bind}
    </div>
  }


  private val testJson =
    """
      |{
      |	"name": "Luke Skywalker",
      |	"height": "172",
      |	"mass": "77",
      |	"hair_color": "blond",
      |	"skin_color": "fair",
      |	"eye_color": "blue",
      |	"birth_year": "19BBY",
      |	"gender": "male",
      |	"homeworld": "https://swapi.co/api/planets/1/",
      |	"films": [
      |		"https://swapi.co/api/films/2/",
      |		"https://swapi.co/api/films/6/",
      |		"https://swapi.co/api/films/3/",
      |		"https://swapi.co/api/films/1/",
      |		"https://swapi.co/api/films/7/"
      |	],
      |	"species": [
      |		"https://swapi.co/api/species/1/"
      |	],
      |	"vehicles": [
      |		"https://swapi.co/api/vehicles/14/",
      |		"https://swapi.co/api/vehicles/30/"
      |	],
      |	"starships": [
      |		"https://swapi.co/api/starships/12/",
      |		"https://swapi.co/api/starships/22/"
      |	],
      |	"created": "2014-12-09T13:50:51.644000Z",
      |	"edited": "2014-12-20T21:17:56.891000Z",
      |	"url": "https://swapi.co/api/people/1/"
      |}
    """.stripMargin

}
