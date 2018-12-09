package pme123.form.shared

import play.api.libs.json.Json
import pme123.form.shared.services.UnitTest

class FormTest extends UnitTest {

  private val category = FormCategory("cats", "Cats", "Various Breeds")
  val formProduct1 = FormProduct("CA-123", "Manx", category)
  val formProduct2 = FormProduct("CA-124", "Persian", category)


  val form = Form("CA-123", "A nice Supercat", 12.5, formProduct1)

  "Form" should "be marshaled and un-marshaled correctly" in {

    Json.toJson(form).validate[Form].get should be(form)
  }
}
