package pme123.form.client.services

import com.thoughtworks.binding.Binding.Var
import pme123.form.shared.services.Language.{DE, EN}
import pme123.form.shared.services.{LoggedInUser, _}
import com.softwaremill.quicklens._

import scala.language.implicitConversions

object UIStore extends Logging {

  implicit val supportedLangs: Seq[Language] = Seq(EN, DE)

  val uiState = UIState()

  lazy val activeLanguage: Var[Language] =
    uiState.activeLanguage

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


  def changeLanguage(language: String): Unit = {
    info(s"FormUIStore: changeLanguage $language")
    val lang = Language.withNameInsensitive(language)
    uiState.activeLanguage.value = lang


    uiState.loggedInUser.value.maybeUser
      .foreach(u =>

        changeLoggedInUser(LoggedInUser(Some(u.modify(_.language).setTo(lang))))

      )
    SemanticUI.initElements()
  }

  case class UIState(
                      loggedInUser: Var[LoggedInUser] = Var(LoggedInUser()),
                      activeLanguage: Var[Language] = Var(EN),
                      webContext: Var[String] = Var("")
                    )

}
