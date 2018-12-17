package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.{I18n, SPAClient, SemanticUI, UIStore}
import pme123.form.shared.{SemanticField, SemanticForm, SemanticRule}

import scala.language.implicitConversions
import scala.scalajs.js.annotation.JSExportTopLevel

object FormClient
  extends SPAClient {


  val mainView: Var[MainView] = UIRoute.route.state

  // @JSExportTopLevel exposes this function with the defined name in Javascript.
  // this is called by the index.scala.html of the server.
  // the only connection that is not type-safe!
  @JSExportTopLevel("client.FormClient.main")
  def main(context: String) {
    initClient(context)
    dom.render(document.getElementById("clientDiv"), render)
    SemanticUI.initElements()
  }

  def render = Binding {
    Constants(
      consumer,
      container
    ).map(_.bind)
  }

  @dom
  private lazy val consumer: Binding[HTMLElement] =
    <div>
      {// no websocket for now: ClientWebsocket.connectConsumerWS.bind
      ""}
    </div>

  @dom
  private lazy val container: Binding[HTMLElement] =
    <div>
      {//
      FormHeader.create().bind}<div class="main-content">
      {//
      mainView.bind.create().bind}
    </div>
    </div>


}


