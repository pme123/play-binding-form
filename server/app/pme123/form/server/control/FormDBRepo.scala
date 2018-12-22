package pme123.form.server.control

import javax.inject.Inject
import pme123.form.server.control.services.DoobieDB

import scala.concurrent.ExecutionContext


class FormDBRepo @Inject()()
                         (implicit val ec:ExecutionContext)
  extends DoobieDB {


}
