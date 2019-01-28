package pme123.form.server.control

import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient
import pme123.form.shared.services.Logging

/**
  * General Test Definition for ScalaTests
  */
trait AcceptanceSpec
  extends WordSpec
   // with AsyncTestSuite
    with TestSuite
    with MustMatchers
    with OptionValues
    with WsScalaTestClient
    with Logging {
}
