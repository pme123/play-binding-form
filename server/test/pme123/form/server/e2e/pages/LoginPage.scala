package pme123.form.server.e2e.pages

import org.openqa.selenium.WebDriver

case class LoginPage()(implicit val webDriver: WebDriver)
  extends BasePage {

  val url: String = baseUrl + "auth/login"


  def setUsername(username: String): Unit =
    textField("username").value = username

  def setPassword(password: String): Unit =
    pwdField("password").value = password

  def clickLogin(): Unit =
    click on buttonWithText("Login")
}


