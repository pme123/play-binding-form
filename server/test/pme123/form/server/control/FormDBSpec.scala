package pme123.form.server.control

import org.scalatest.concurrent.ScalaFutures

class FormDBSpec
  extends GuiceAcceptanceSpec
    with ScalaFutures {

  private lazy val formDBRepo = inject[FormDBRepo]

  "FormDB" should {

    "initialize the Form Table" in {
      whenReady(formDBRepo.selectForm("address")) { f =>
        assert(f.ident == "address")
      }
    }

  }
}


