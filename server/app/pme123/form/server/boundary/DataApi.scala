package pme123.form.server.boundary

import java.nio.file.Files

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import pme123.data.server.control.DataDBRepo
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.entity.JsonParseException
import pme123.form.shared._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class DataApi @Inject()(dataDBRepo: DataDBRepo,
                        val spaComps: SPAComponents)
                       (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  val fileName = "importDataStructure"

  def persistData(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asText.map { body =>
      Json.parse(body).validate[DataContainer] match {
        case JsSuccess(data, _) =>
          dataDBRepo.persist(data)
            .map(data =>
              Ok(Json.toJson(data)).as(JSON))
        case err: JsError =>
          Future.successful(BadRequest(s"Problem parsing Datacontainer: $err"))
      }
    }.getOrElse(Future.successful(BadRequest("No Data in Body!")))
  }

  def importDataStructure(): Action[AnyContent] = Action.async { implicit request =>
    Future(
      extractFile(request)
        .getOrElse(DataContainer()))
      .map(ds =>
        Ok(Json.toJson(ds)).as(JSON)
      )
  }

  private def extractFile(request: Request[AnyContent]) = {
    request.body.asMultipartFormData.flatMap {
      data =>
        data.file(fileName).flatMap {
          file =>
            if (Files.size(file.ref.path) == 0)
              None // no file is present
            else {
              val lines = Files.readAllLines(file.ref.path).asScala
              Some(
                Json.parse(lines.mkString("\n")).validate[DataContainer] match {
                  case JsSuccess(ds, _) => ds
                  case err: JsError => throw JsonParseException(err)
                }
              )
            }
        }
    }
  }
}

