package pme123.form.client.services

import com.thoughtworks.binding.Binding.Var
import pme123.form.shared.services.{LoggedInUser, _}

import scala.language.implicitConversions

object UIStore extends Logging {

  val uiState = UIState()

  def changeWebContext(webContext: String): String = {
    info(s"UIStore: changeWebContext $webContext")
    uiState.webContext.value = webContext
    webContext
  }

  def changeLoggedInUser(loggedInUser: LoggedInUser): LoggedInUser = {
    info(s"UIStore: changeLoggedInUser $loggedInUser")
    uiState.loggedInUser.value = loggedInUser
    loggedInUser
  }

  case class UIState(
                      loggedInUser: Var[LoggedInUser] = Var(LoggedInUser()),
                      webContext: Var[String] = Var("")
                    )

}
