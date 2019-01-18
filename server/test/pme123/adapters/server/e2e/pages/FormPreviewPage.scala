package pme123.adapters.server.e2e.pages

import org.openqa.selenium.WebDriver

case class FormPreviewPage()(implicit val webDriver: WebDriver)
  extends BasePage {

  val url: String = baseUrl + "#preview"


  def selectForm(formIdent: String): Unit =
    singleSel("formIdent").value = formIdent
}


