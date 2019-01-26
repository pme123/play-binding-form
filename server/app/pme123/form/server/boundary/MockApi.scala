package pme123.form.server.boundary

import javax.inject._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.{MockDBRepo, MockService}
import pme123.form.shared.MockContainer

import scala.concurrent.{ExecutionContext, Future}

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class MockApi @Inject()(jsonService: MockService,
                        mockDBRepo: MockDBRepo,
                        val spaComps: SPAComponents)
                       (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  def callService(base64UrlStr: String): Action[AnyContent] = SecuredAction.async { implicit request: Request[AnyContent] =>
    jsonService.callService(base64UrlStr)
      .map(Ok(_).as(JSON))
  }

  def persistMock(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asText.map { body =>
      Json.parse(body).validate[MockContainer] match {
        case JsSuccess(mock, _) =>
          mockDBRepo.persist(mock)
            .map(mock =>
              Ok(Json.toJson(mock)).as(JSON))
        case err: JsError =>
          Future.successful(BadRequest(s"Problem parsing MockContainer: $err"))
      }
    }.getOrElse(Future.successful(BadRequest("No Mock in Body!")))
  }

  def idents(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    mockDBRepo.idents()
      .map(ids => Ok(Json.toJson(ids)).as(JSON))
  }

  def mock(ident: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    mockDBRepo.selectMock(ident)
      .map(mc => Ok(Json.toJson(mc)).as(JSON))
  }
}


