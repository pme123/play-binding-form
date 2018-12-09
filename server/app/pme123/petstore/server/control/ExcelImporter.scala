package pme123.form.server.control

import javax.inject.{Inject, Singleton}
import pme123.form.server.control.services.UserDBRepo
import pme123.form.shared.services.Logging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class ExcelImporter @Inject()(formDBInitializer: FormDBInitializer,
                              userDBRepo: UserDBRepo,
                              formDBRepo: FormDBRepo) // to make sure it is initialized
                             (implicit val ec: ExecutionContext)
  extends Logging {

  import pme123.form.server.entity.ImportWorkbook._

  importCategories
  importProducts
  importForms
  importUsers

  private def importCategories = {
    workbook.categories match {
      case Success(categories) =>
        categories.map {
          case Success(cat) => formDBRepo.insertCategory(cat)
          case Failure(exc) => error(exc)
        }
      case Failure(exc) => error(exc)
    }
  }

  private def importProducts = {
    workbook.formProducts match {
      case Success(formProducts) =>
        formProducts.map {
          case Success(prod) => formDBRepo.insertProduct(prod)
          case Failure(exc) => error(exc)
        }
      case Failure(exc) => error(exc)
    }
  }

  private def importForms = {
    workbook.forms match {
      case Success(forms) =>
        forms.map {
          case Success(form) => formDBRepo.insertForm(form)
          case Failure(exc) => error(exc)
        }
      case Failure(exc) => error(exc)
    }
  }

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
