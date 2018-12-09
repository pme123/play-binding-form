package pme123.form.shared.services

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

trait UnitTest
  extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with BeforeAndAfterAll
    with Logging {

}
