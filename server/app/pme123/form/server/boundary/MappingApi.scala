package pme123.form.server.boundary

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.{DataDBRepo, FormDBRepo, MappingDBRepo}
import pme123.form.shared._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class MappingApi @Inject()(mappingDBRepo: MappingDBRepo,
                           formDBRepo: FormDBRepo,
                           dataDBRepo: DataDBRepo,
                           val spaComps: SPAComponents)
                          (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  val fileName = "importMappingStructure"

  def persistMapping(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asText.map { body =>
      Json.parse(body).validate[MappingContainer] match {
        case JsSuccess(mapping, _) =>
          mappingDBRepo.persist(mapping)
            .map(mapping =>
              Ok(Json.toJson(mapping)).as(JSON))
        case err: JsError =>
          Future.successful(BadRequest(s"Problem parsing Mappingcontainer: $err"))
      }
    }.getOrElse(Future.successful(BadRequest("No Mapping in Body!")))
  }

  def idents(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    mappingDBRepo.idents()
      .map(ids => Ok(Json.toJson(ids)).as(JSON))
  }

  def mapping(ident: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    for{
      mapping <- mappingDBRepo.selectMapping(ident)
      form <- formDBRepo.selectForm(mapping.formIdent)
      data <- dataDBRepo.selectData(mapping.dataIdent)
      getMapping = GetMappingContainer(mapping.ident, form, data, mapping.mappings)
    }yield Ok(Json.toJson(getMapping)).as(JSON)
  }
}


