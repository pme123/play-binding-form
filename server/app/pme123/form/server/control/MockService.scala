package pme123.form.server.control

import java.nio.charset.StandardCharsets

import com.github.marklister.base64.Base64._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import pme123.form.shared.MockEntry

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class MockService @Inject()(val ws: WSClient,
                           )(implicit val ec: ExecutionContext) {

  def callService(base64UrlStr: String): Future[MockEntry] = {
    val url = new String(base64UrlStr.toByteArray(base64Url), StandardCharsets.UTF_8)
    ws.url(url)
      .get()
      .map { resp =>
        resp.status
        MockEntry(Random.nextInt(1000), url, resp.status, Json.prettyPrint(resp.json))
      }
  }

}