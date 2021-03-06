package pme123.form.server.boundary

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.FormDBRepo
import pme123.form.shared._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class FormApi @Inject()(formDBRepo: FormDBRepo,
                        val spaComps: SPAComponents)
                       (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  def persistForm(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asText.map { body =>
        Json.parse(body).validate[FormContainer] match {
          case JsSuccess(form, _) =>
            formDBRepo.persist(form)
              .map(form =>
                Ok(Json.toJson(form)).as(JSON))
          case err: JsError =>
            Future.successful(BadRequest(s"Problem parsing Formcontainer: $err"))
        }
    }.getOrElse(Future.successful(BadRequest("No Form in Body!")))
  }

  def idents(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.idents()
      .map(ids => Ok(Json.toJson(ids)).as(JSON))
  }

  def form(ident: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.selectForm(ident)
      .map(form => Ok(Json.toJson(form)).as(JSON))
  }
}
