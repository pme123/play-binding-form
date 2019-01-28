package pme123.form.server.boundary

import javax.inject._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.MockService

import scala.concurrent.ExecutionContext

@Singleton
class MockTestApi @Inject()(mockService: MockService,
                            val spaComps: SPAComponents)
                           (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  def getCall(serviceConf: String, path: String): Action[AnyContent] = SecuredAction.async { implicit request: Request[AnyContent] =>
    mockService.getCall(serviceConf, path)
        .map(resp => Status(resp.status)(resp.body).as(JSON))
  }

}


