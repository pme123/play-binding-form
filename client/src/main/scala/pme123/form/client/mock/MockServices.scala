package pme123.form.client.mock

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import play.api.libs.json.{JsValue, Json}
import pme123.form.client.services.HttpServices
import pme123.form.shared.{MockContainer, MockEntry, ServiceRequest}

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object MockServices
  extends HttpServices {

  def callService(serviceRequest: ServiceRequest): Binding[HTMLElement] = {
    val path = s"$apiPath/mock/callService"

    httpPut(path, serviceRequest, (mock: MockEntry) => {
      UIMockStore.changeMockEntry(mock)
    })
  }

  def persistMock(mockContainer: MockContainer): Binding[HTMLElement] = {
    val path = s"$apiPath/mock"

    httpPost(path, mockContainer, (_: MockContainer) => () // nothing to do
    )
  }

  def idents(): Binding[HTMLElement] = {
    val path = s"$apiPath/mock/idents"

    httpGet(path, (ids: Seq[String]) => UIMockStore.changeIdents(ids))
  }

  def getMock(ident: String): Binding[HTMLElement] = {
    val path = s"$apiPath/mock/$ident"

    httpGet(path, (mock: MockContainer) => UIMockStore.changeMock(mock))
  }

  def testService(serviceRequest: ServiceRequest): Binding[HTMLElement] = {
    val path = s"$apiPath/mock/test/${serviceRequest.serviceConf}/${serviceRequest.path}"
        .replace("//", "/")

    httpGet(path, (body: JsValue) =>
      UIMockTestStore.uiState.varServiceRequest.value.varPayload.value = Some(Json.prettyPrint(body)))
  }



}
