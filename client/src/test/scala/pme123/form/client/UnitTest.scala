package pme123.form.client

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}
import pme123.form.shared.services.Logging

trait UnitTest
  extends FlatSpec
      with Matchers
      with BeforeAndAfter
      with BeforeAndAfterAll
      with Logging {

  }
