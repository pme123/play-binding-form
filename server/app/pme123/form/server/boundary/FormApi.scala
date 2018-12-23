package pme123.form.server.boundary

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.FormDBRepo
import pme123.form.shared._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class FormApi @Inject()(formDBRepo: FormDBRepo,
                        val spaComps: SPAComponents)
                       (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  def persistForm() = Action.async { implicit request: Request[AnyContent] =>
    val eventualResult: Future[Result] = request.body.asText.map { body =>
      val eventualResult: Future[Result] =
        Json.parse(body).validate[FormContainer] match {
          case JsSuccess(form, _) =>
            formDBRepo.persist(form)
              .map(form =>
                Ok(Json.toJson(form)).as(JSON))
          case err: JsError =>
            Future.successful(BadRequest(s"Problem parsing Formcontainer: ${err}"))
        }
      eventualResult
    }.getOrElse(Future.successful(BadRequest("No Form in Body!")))
    eventualResult
  }

  def forms(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    Ok(Json.toJson(FormContainer("form-id"))).as(JSON)
  }

  def formIds(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.formIds()
      .map(ids => Ok(Json.toJson(ids)).as(JSON))
  }

}
