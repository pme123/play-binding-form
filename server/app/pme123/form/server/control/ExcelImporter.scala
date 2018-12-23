package pme123.form.server.control

import javax.inject.{Inject, Singleton}
import pme123.form.server.control.services.UserDBRepo
import pme123.form.shared.services.Logging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import pme123.form.server.entity.ImportWorkbook.workbook

@Singleton
class ExcelImporter @Inject()(userDBRepo: UserDBRepo) // to make sure it is initialized
                             (implicit val ec: ExecutionContext)
  extends Logging {

  importUsers

  private def importUsers = {
    workbook.users match {
      case Success(users) =>
        users.map {
          case Success(user) => userDBRepo.insertUser(user)
          case Failure(exc) => error(exc)
        }
      case Failure(exc) => error(exc)
    }
  }
}
