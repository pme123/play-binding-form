package pme123.adapters.server.e2e.tests

import java.net.URL

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.{LocalFileDetector, RemoteWebDriver}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, Matchers, Suite, WordSpec}
import pme123.adapters.server.e2e.E2EConfig
import pme123.adapters.server.e2e.pages.LoginPage

import scala.collection.JavaConverters._

trait FormE2E
  extends WordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with WebBrowser
    with E2EConfig {
  this: Suite =>

  private val prefs = Map("intl.accept_languages" -> "en")

  private def options =
    new ChromeOptions()
      .setExperimentalOption("prefs", prefs.asJava)
      .addArguments("start-maximized")

  private def headlessOptions = options.addArguments("headless")

  def createWebDriver(): WebDriver = {
    val driver = new RemoteWebDriver(new URL(webDriverUrl),
      if (interactiveMode) options else headlessOptions)

    driver.setFileDetector(new LocalFileDetector())
    driver
  }

  implicit lazy val webDriver: WebDriver = createWebDriver()

  protected val demoAdmin = "demoAdmin"

  def loggedInUser(implicit webDriver: WebDriver): Option[String] =
    find(xpath("//meta[@name='application-name']"))
      .flatMap(_.attribute("data-userid"))

  "As a preparation BPF" should {
    "login as adminUser" in {
      go to baseUrl
      login(demoAdmin,demoAdmin)
      loggedInUser shouldBe Some(demoAdmin)
    }
  }

  def login(username: String, password: String)(implicit webDriver: WebDriver): Unit = {
    val initialUrl = currentUrl

    val page = LoginPage()

    go to page
    page.setUsername(username)
    page.setPassword(password)
    page.clickLogin()

    go to initialUrl
  }

}
