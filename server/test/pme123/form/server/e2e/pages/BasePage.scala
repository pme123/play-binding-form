package pme123.form.server.e2e.pages

import org.scalatest.selenium.{Page, WebBrowser}
import pme123.form.server.e2e.E2EConfig

abstract class BasePage
  extends Page
    with WebBrowser
    with E2EConfig {

  def buttonWithText(name: String): Query =
    xpath(s"//button[contains(text(),'${escapeXPath(name)}')]")

  private def escapeXPath(name: String): String =
    name.replace("'", "''")
}