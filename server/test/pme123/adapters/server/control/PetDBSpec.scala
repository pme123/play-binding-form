package pme123.adapters.server.control

import org.scalatest.TestSuite
import pme123.form.server.control.FormDBRepo

class FormDBSpec
  extends GuiceAcceptanceSpec {

  private lazy val formDBRepo = inject[FormDBRepo]

  "FormDB" should {

    "initialize the Category Table" in {
      formDBRepo.selectCategories()
        .map(categories =>
          assert(categories.size >= 5))
    }
    "initialize the Product Table" in {
      formDBRepo.selectProducts()
        .map(products =>
          assert(products.size >= 15))
    }
    "initialize the Form Table" in {
      formDBRepo.selectForms()
        .map(forms =>
          assert(forms.size >= 15))
    }

  }
}


