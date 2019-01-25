package pme123.form.client.json

import com.github.marklister.base64.Base64._
import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import play.api.libs.json.{JsValue, Json}
import pme123.form.client.services.HttpServices

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object JsonServices
  extends HttpServices {

  def getJson(url: String): Binding[HTMLElement] = {
    val path = s"$apiPath/json/${url.getBytes("UTF-8").toBase64(base64Url)}"

    httpGet(path, (json: JsValue) => {
      UIJsonStore.uiState.jsonVar.value = Some(Json.prettyPrint(json))
    })
  }


}
