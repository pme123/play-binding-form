package pme123.adapters.server.e2e.tests

import org.openqa.selenium.By
import pme123.adapters.server.e2e.pages.FormPreviewPage

import scala.collection.JavaConverters._

// this Spec also tests the RestServiceTask
class FormPreviewE2E
  extends FormE2E {

  "The Form Preview" should {
    lazy val formPreviewPage = FormPreviewPage()

    "be displayed if we go to" in {
      go to formPreviewPage
      pageTitle shouldBe "Form Store - Play Binding Demo"
      webDriver
        .findElements(By.cssSelector("h3.header")).asScala
        .map(_.getText)
        .head.trim shouldBe "Form Preview"
    }
  }

}


