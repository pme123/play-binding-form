package pme123.adapters.server.control

import org.scalatest.concurrent.ScalaFutures
import pme123.form.server.control.FormDBRepo

class FormDBSpec
  extends GuiceAcceptanceSpec
    with ScalaFutures {

  private lazy val formDBRepo = inject[FormDBRepo]

  "FormDB" should {

    "initialize the Form Table" in {
      whenReady(formDBRepo.selectForm("address-form")) { f =>
        assert(f.ident == "address-form")
      }
    }

  }
}


