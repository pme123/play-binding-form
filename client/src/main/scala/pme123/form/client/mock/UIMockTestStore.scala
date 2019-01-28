package pme123.form.client.mock

import com.thoughtworks.binding.Binding.Var
import pme123.form.client.services.SemanticUI
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIMockTestStore extends Logging {


  val uiState = UIState()

  def changeMockIdent(ident: String): Unit = {
    info(s"UIMockTestStore: changeMockIdent (serviceConfig) $ident")
    uiState.varIdent.value = ident
    SemanticUI.initElements()
  }

  case class UIState(
                      varIdent: Var[String],
                      varServiceRequest: Var[VarServiceRequest],
                    )

  object UIState {

    def apply(varServiceConf: Var[String] = UIMockStore.uiState.varIdent): UIState = {

      UIState(
        varServiceConf,
        Var(VarServiceRequest(varServiceConf))
      )
    }
  }

  case class VarServiceRequest(varServiceConf: Var[String] = Var(""),
                               varPath: Var[String] = Var(""),
                               varHttpMethod: Var[HttpMethod] = Var(HttpMethod.GET),
                               varPayload: Var[Option[String]] = Var(None),
                              ) {
    def toServiceRequest: ServiceRequest = {
      ServiceRequest(
        varServiceConf.value,
        varPath.value,
        varHttpMethod.value,
        varPayload.value,
      )
    }
  }

  object VarServiceRequest {

    def apply(mock: ServiceRequest): VarServiceRequest =
      VarServiceRequest(
        Var(mock.serviceConf),
        Var(mock.path),
        Var(mock.httpMethod),
        Var(mock.payload),
      )

  }

}