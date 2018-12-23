package pme123.form.server.control

import doobie.implicits._
import doobie.util.fragment.Fragment
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import pme123.form.server.control.services.DoobieDB
import pme123.form.server.entity.JsonParseException
import pme123.form.shared.FormContainer

import scala.concurrent.{ExecutionContext, Future}


class FormDBRepo @Inject()()
                          (implicit val ec: ExecutionContext)
  extends DoobieDB {


  def persist(form: FormContainer): Future[FormContainer] = {
    selectMaybeForm(form.formId)
      .flatMap {
        case Some(sForm) =>
          updateForm(form)
        case None =>
          insertForm(form)
      }.map(_ => form)
  }

  def formIds(): Future[List[String]] = {
    selectForms()
      .map(_.map(_.formId))
  }

  def insertForm(formCont: FormContainer): Future[Int] = {
    val formContent = Json.toJson(formCont).toString()
    update(
      sql"""insert into form (formId, content)
             values (${formCont.formId}, $formContent)""")
  }

  def updateForm(formCont: FormContainer): Future[Int] = {
    val formContent = Json.toJson(formCont).toString()
    update(
      sql"""update form
              set content = $formContent
             where formId = ${formCont.formId}""")
  }

  def selectForm(formId: String): Future[FormContainer] =
    selectMaybeForm(formId).map(_.get)

  def selectMaybeForm(formId: String): Future[Option[FormContainer]] =
    selectForms(fr"where f.formId = $formId")
      .map(_.headOption)

  private def selectForms(where: Fragment = fr""): Future[List[FormContainer]] =
    select((fr"""select f.formId, f.content
                     from form f
         """ ++ where)
      .query[(String, String)]
      .map { case (formId, content) =>
        println(s"Form selected from DB: $formId")
        Json.parse(content)
          .validate[FormContainer] match {
          case JsSuccess(value, _) =>
            value
          case error: JsError =>
            throw JsonParseException(error)
        }
      }
    )


}
