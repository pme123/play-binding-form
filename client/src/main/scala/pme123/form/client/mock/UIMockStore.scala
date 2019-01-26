package pme123.form.client.mock

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.util.Random

object UIMockStore extends Logging {


  val uiState = UIState()

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"MockVarStore: changeIdents $idents")
    uiState.varsIdents.value.clear()
    uiState.varsIdents.value ++= idents
    SemanticUI.initElements()
  }

  def changeMock(mock: MockContainer): Unit = {
    info(s"MockVarStore: changeMock ${mock.ident}")
    changeMockIdent(mock.ident)
    uiState.varsMockEntries.value.clear()
    uiState.varsMockEntries.value ++=
      mock.mocks.map(VarMockEntry.apply)
    SemanticUI.initElements()
  }

  def changeMockIdent(ident: String): Unit = {
    info(s"MockVarStore: changeMockIdent $ident")
    uiState.varIdent.value = ident
    SemanticUI.initElements()
  }

  def addMockEntry(): VarMockEntry = {
    info(s"MockVarStore: addMockEntry")
    val entry = VarMockEntry()
    uiState.varsMockEntries.value += entry
    changeSelectedEntry(entry)
    entry
  }

  def changeSelectedEntry(varMockEntry: VarMockEntry): Unit = {
    info(s"MockVarStore: changeSelectedEntry ${url(varMockEntry)}")
    uiState.varSelectedEntry.value = varMockEntry
    SemanticUI.initElements()
  }

  def copySelectedEntry(varMockEntry: VarMockEntry): Unit = {
    info(s"MockUIStore: copySelectedEntry ${url(varMockEntry)}")
    val mock = varMockEntry.toMock
    val newVarEntry = VarMockEntry(mock.copy(url = mock.url + Random.nextInt(100)))
    println(s"NEWVARENTRY: $newVarEntry")
    println(s"VARMOCKENTRY: $varMockEntry")

    uiState.varSelectedEntry.value = newVarEntry
    uiState.varsMockEntries.value.insert(
      uiState.varsMockEntries.value.indexOf(varMockEntry) + 1,
      newVarEntry)
    SemanticUI.initElements()
  }

  def deleteSelectedEntry(varMockEntry: VarMockEntry): Unit = {
    info(s"MockUIStore: deleteSelectedEntry ${url(varMockEntry)}")
    if (uiState.varSelectedEntry.value == varMockEntry) {
      val newElem = uiState.varsMockEntries.value.headOption
        .getOrElse {
          addMockEntry()
        }
      changeSelectedEntry(newElem)
    }
    uiState.varsMockEntries.value -= varMockEntry
  }

  def toMock: MockContainer = {
    MockContainer(uiState.varIdent.value,
      uiState.varsMockEntries.value.map(_.toMock),
    )
  }

  private def url(varMockEntry: VarMockEntry) = varMockEntry.varUrl.value

  case class UIState(
                      varIdent: Var[String],
                      varsMockEntries: Vars[VarMockEntry],
                      varsIdents: Vars[String],
                      varSelectedEntry: Var[VarMockEntry],
                    )

  object UIState {

    def apply(varIdent: Var[String] = Var(MockContainer.defaultIdent)): UIState = {
      val entry = VarMockEntry(MockEntry(testUrl, testJson))
      UIState(
        varIdent,
        Vars(entry),
        Vars.empty,
        Var(entry),
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

  case class VarMockEntry(varUrl: Var[String] = Var(""),
                          varContent: Var[String] = Var("")) {
    def toMock: MockEntry = {
      MockEntry(
        varUrl.value,
        varContent.value,
      )
    }
  }

  object VarMockEntry {

    def apply(mock: MockEntry): VarMockEntry =
      VarMockEntry(Var(mock.url),
        Var(mock.content)
      )

  }

}