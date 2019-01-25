package pme123.form.server.boundary

import javax.inject._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.JsonService

import scala.concurrent.ExecutionContext

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class JsonApi @Inject()(jsonService: JsonService,
                        val spaComps: SPAComponents)
                       (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  val fileName = "importDataStructure"

  def callService(base64UrlStr: String): Action[AnyContent] = SecuredAction.async { implicit request: Request[AnyContent] =>
    jsonService.callService(base64UrlStr)
      .map(Ok(_).as(JSON))
  }
}


