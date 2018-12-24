package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import play.api.libs.json.JsString
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.language.implicitConversions
import scala.util.Random


object UIDataStore extends Logging {

  val uiState = UIState()


  case class UIState(
                      dataStructure: Var[DataStructure],
                    )

  object UIState {
    def apply(): UIState = {
      UIState(
        Var(DataStructure(s"data-${Random.nextInt(1000)}", StructureType.STRING, JsString(""))),
      )
    }
  }


}
