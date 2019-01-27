package pme123.form.server.control

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import pme123.form.shared.{MockEntry, ServiceRequest}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class MockService @Inject()(val ws: WSClient,
                            formConfiguration: FormConfiguration,
                           )(implicit val ec: ExecutionContext) {

  def callService(serviceRequest: ServiceRequest): Future[MockEntry] = {
    val conf = formConfiguration.serviceMap(serviceRequest.serviceConf)
    val url = conf.url + serviceRequest.path
    // only GET supported
    ws.url(url)
      .get()
      .map { resp =>
        resp.status
        MockEntry(Random.nextInt(1000), serviceRequest.path, resp.status, Json.prettyPrint(resp.json))
      }
  }

}