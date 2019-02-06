package pme123.form.server.control

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}

class FormDBSpec
  extends GuiceAcceptanceSpec
    with ScalaFutures {

  private lazy val formDBRepo = inject[FormDBRepo]

  "FormDB" should {

    "initialize the Form Table" in {
      whenReady(formDBRepo.selectForm("address"), Timeout(Span(2, Seconds))) { f =>
        assert(f.ident == "address")
      }
    }

  }
}


