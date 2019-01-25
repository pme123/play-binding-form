package pme123.form.client.json

import com.thoughtworks.binding.Binding.Var
import pme123.form.shared.services.Logging

object UIJsonStore extends Logging {


  val uiState = UIState()

  case class UIState(
                      urlVar: Var[Option[String]],
                      jsonVar: Var[Option[String]],
                    )

  object UIState {

    def apply(): UIState = {
      UIState(
        Var(Some(testUrl)),
        Var(Some(testJson)),
      )
    }

    private val testUrl = "https://swapi.co/api/people/1"
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

}