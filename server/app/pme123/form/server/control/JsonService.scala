package pme123.form.server.control

import java.nio.charset.StandardCharsets

import com.github.marklister.base64.Base64._
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

class JsonService @Inject()(val ws: WSClient,
                            )(implicit val ec: ExecutionContext) {

  def callService(base64UrlStr: String): Future[JsValue] = {
    val url = new String(base64UrlStr.toByteArray(base64Url), StandardCharsets.UTF_8)
    ws.url(url)
      .get()
      .map(_.json)

  }

}