package pme123.form.server.control

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import pme123.form.server.entity.{HttpResponse, ServiceException, UrlMatcher}
import pme123.form.shared.{MockEntry, ServiceRequest}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class MockService @Inject()(val ws: WSClient,
                            mockDBRepo: MockDBRepo,
                            formConfiguration: FormConfiguration,
                           )(implicit val ec: ExecutionContext) {

  def getCall(serviceConf: String, path: String): Future[HttpResponse] = {
    mockDBRepo.selectMaybeMock(serviceConf). map{
      case Some(mock) => UrlMatcher.getResponse(mock, path)
      case None => HttpResponse(404, s"No Mock Config found for $path")
    }
  }

  def callService(serviceRequest: ServiceRequest): Future[MockEntry] = {
    val conf = formConfiguration.serviceMap.getOrElse(serviceRequest.serviceConf, throw ServiceException(s"There is no ServiceConf for '${serviceRequest.serviceConf}'"))
    val url = conf.url + s"/${serviceRequest.path}".replace("//", "/")
    ws.url(url)
      .get()
      .map { resp =>
        resp.status
        MockEntry(Random.nextInt(1000), serviceRequest.path, resp.status, Json.prettyPrint(resp.json))
      }
  }

}