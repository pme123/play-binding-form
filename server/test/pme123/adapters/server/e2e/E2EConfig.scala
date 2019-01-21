package pme123.adapters.server.e2e

import java.net.URL

import com.typesafe.config.{Config, ConfigFactory}

trait E2EConfig {
  val webDriverUrl: URL = E2EConfig.webDriverUrl
  val baseUrl: String = E2EConfig.baseUrl
  val interactiveMode: Boolean = E2EConfig.interactiveMode
}

object E2EConfig {
  private lazy val configuration = ConfigFactory.load()

  /**
    * path to the selenium web driver url
    */
  def webDriverUrl: URL = new URL(configuration.getString("pme123.form.e2e.webDriverUrl"))

  /**
    * base url to the application under test
    */
  def baseUrl = configuration.getString("pme123.form.e2e.baseUrl")

  /**
    * In interactive mode, the e2e tests are executed visible and browser is not closed at the end.
    */
  def interactiveMode = configuration.getBoolean("pme123.form.e2e.interactiveMode")
}