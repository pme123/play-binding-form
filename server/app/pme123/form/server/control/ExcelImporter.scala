package pme123.form.server.control

import javax.inject.{Inject, Singleton}
import pme123.form.server.control.services.UserDBRepo
import pme123.form.server.entity.ImportWorkbook.workbook
import pme123.form.shared.services.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class ExcelImporter @Inject()(userDBRepo: UserDBRepo,
                              formDBRepo: FormDBRepo,
                              dataDBRepo: DataDBRepo,
                              mappingDBRepo: MappingDBRepo,
                              mocksDBRepo: MockDBRepo,
                             ) // to make sure it is initialized
                             (implicit val ec: ExecutionContext)
  extends Logging {

  importObjects(workbook.users, userDBRepo.insertUser)
  importObjects(workbook.forms, formDBRepo.insertForm)
  importObjects(workbook.data, dataDBRepo.insertData)
  importObjects(workbook.mappings, mappingDBRepo.insertMapping)
  importObjects(workbook.mocks, mocksDBRepo.insertMock)

  private def importObjects[T](objects: Try[Seq[Try[T]]], insert: T => Future[_]) = {
    objects match {
      case Success(objs) =>
        objs.map {
          case Success(obj) =>
            insert(obj)
          case Failure(exc) =>
            error(exc)
        }
      case Failure(exc) =>
        error(exc)
    }
  }
}
