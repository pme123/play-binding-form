package pme123.form.server.boundary

import cats.data.NonEmptyList
import doobie._
import doobie.implicits._
import doobie.util.fragment.Fragment
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

  def forms(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

        Ok(Json.toJson(FormContainer())).as(JSON)
  }
}
