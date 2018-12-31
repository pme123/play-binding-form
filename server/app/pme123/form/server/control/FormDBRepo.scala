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
    selectMaybeForm(form.ident)
      .flatMap {
        case Some(sForm) =>
          updateForm(form)
        case None =>
          insertForm(form)
      }.map(_ => form)
  }

  def idents(): Future[List[String]] = {
    selectForms()
      .map(_.map(_.ident))
  }

  def insertForm(formCont: FormContainer): Future[Int] = {
    val formContent = Json.toJson(formCont).toString()
    update(
      sql"""insert into form (ident, content)
             values (${formCont.ident}, $formContent)""")
  }

  def updateForm(formCont: FormContainer): Future[Int] = {
    val formContent = Json.toJson(formCont).toString()
    update(
      sql"""update form
              set content = $formContent
             where ident = ${formCont.ident}""")
  }

  def selectForm(ident: String): Future[FormContainer] =
    selectMaybeForm(ident).map(_.get)

  def selectMaybeForm(ident: String): Future[Option[FormContainer]] =
    selectForms(fr"where f.ident = $ident")
      .map(_.headOption)

  private def selectForms(where: Fragment = fr""): Future[List[FormContainer]] =
    select((fr"""select f.ident, f.content
                     from form f
         """ ++ where)
      .query[(String, String)]
      .map { case (_, content) =>
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
