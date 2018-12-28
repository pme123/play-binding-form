package pme123.form.client

import com.thoughtworks.binding.Route
import org.scalajs.dom.window
import pme123.form.client.FormClient.info
import pme123.form.client.data.DataView
import pme123.form.client.mapping.MappingView
import pme123.form.client.services.SemanticUI

object UIRoute {


  def createView(hashText: String): MainView = {
    hashText match {
      case FormEditorView.hashRegex() =>
        FormEditorView
      case FormPreviewView.hashRegex() =>
        FormPreviewView
      case DataView.hashRegex() =>
        DataView
      case MappingView.hashRegex() =>
        MappingView
      case _ =>
        info(s"FormEditorView!!: $hashText")
        FormEditorView
    }
  }

  def changeRoute(view: MainView) {
    if (route.state.value != view) {
      route.state.value = view
      SemanticUI.initElements()
    }
  }


  info("org.scalajs.dom.window.location: " + window.location)

  val route: Route.Hash[MainView] = Route.Hash[MainView](createView(window.location.hash))(
    new Route.Format[MainView] {
      override def unapply(hashText: String): Option[MainView] = {
        Some(createView(hashText))
      }

      override def apply(state: MainView): String = {
        info(s"state.name > ${state.link}")
        state.link
      }
    }
  )

  route.watch()
}
