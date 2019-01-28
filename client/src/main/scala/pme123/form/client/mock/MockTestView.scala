package pme123.form.client.mock

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client._
import pme123.form.client.form.UIFormElem
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{DROPDOWN, TEXTAREA, TEXTFIELD}
import pme123.form.shared.ExtraProp.ROWS
import pme123.form.shared._

private[client] object MockTestView
  extends MockyView {

  val link = "mock-test"
  val icon = "bug"

  val runTestFlag = Var(false)
  val varUrl = Var("")

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      {<div class="ui form">
      {//
      header(
        UIMockTestStore.uiState.varIdent,
        Some(UIMockTestStore.changeMockIdent),
        runTestButton,
      ).bind}{//
      runTestDiv.bind}{//
      content.bind}<div class="ui error message"></div>
    </div>}

    </div>

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val runTestButton: Binding[HTMLElement] = {
    <div class="item">
      <button class="ui circular blue icon button"
              data:data-tooltip="Test Mock Konfiguration (the configuration must be in the Mock Repo!)"
              onclick={_: Event =>
                runTestFlag.value = true}>
        <i class="play icon"></i>
      </button>
    </div>
  }

  @dom
  private lazy val content: Binding[HTMLElement] = {
    <div class="ui container">
      <div class="ui cards">
        {element.bind}
      </div>
    </div>
  }

  @dom
  private lazy val element: Binding[HTMLElement] = {
    val serviceRequest = UIMockTestStore.uiState.varServiceRequest.bind
    <div class="ui fluid card">
      <div class="extra content">
        <div class="ui grid">
          <div class="eleven wide column">
            {val path = serviceRequest.varPath.bind
          BaseElementDiv(
            UIFormElem(BaseElement(
              "service-request-url",
              TEXTFIELD,
              ElementTexts.placeholder(I18n("json.import")),
              extras = ExtraProperties(),
              value = Some(path),
              required = true,
            ), Some { str =>
              serviceRequest.varPath.value = str
            }
            )).bind}
          </div> <div class="two wide column">
          {//
          val httpMethod = serviceRequest.varHttpMethod.bind
          BaseElementDiv(
            UIFormElem(BaseElement(
              "service-request-http-method",
              DROPDOWN,
              ElementTexts.placeholder(I18n("mock.entry.http.method")),
              extras = ExtraProperties(),
              elemEntries = ElementEntries.simple(HttpMethod.values),
              value = Some(httpMethod.toString),
            ), Some { str =>
              serviceRequest.varHttpMethod.value = HttpMethod.withNameInsensitive(str)
            }
            )).bind}
        </div> <div class="three wide column">

        </div>
        </div>
      </div>{//
      val maybeContent = serviceRequest.varPayload.bind
      val content = maybeContent.getOrElse("")
      val elemId = s"service-request-payload"
      MockUtils.initCodeField(elemId, str =>
        serviceRequest.varPayload.value =
          if (str.nonEmpty)
            Some(str)
          else
            None)
      BaseElementDiv(
        UIFormElem(BaseElement(
          elemId,
          TEXTAREA,
          ElementTexts.placeholder(I18n("service.request.payload")),
          extras = ExtraProperties(Seq(
            ExtraPropValue(
              ROWS, "50"
            ))),
          value = Some(content),
          required = true,
        )
        )).bind}
    </div>
  }

  @dom
  private lazy val runTestDiv: Binding[HTMLElement] = {
    val doCall = runTestFlag.bind
    if (doCall)
      <div>
        {runTestFlag.value = false
      val serviceRequest = UIMockTestStore.uiState.varServiceRequest.value
      MockServices.testService(serviceRequest.toServiceRequest).bind}
      </div>
    else
        <span/>
  }

}
