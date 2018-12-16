package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement

import scala.util.matching.Regex

private[client] object FormPreviewView
  extends MainView {

  val hashRegex: Regex = """#preview""".r

  def name: String = "preview"

  val link: String = name

  // 1. level of abstraction
  // **************************
  @dom
  def create(): Binding[HTMLElement] =
    <div class="ui container">
      <div class="ui form">
      {//
      previewContent.bind}
      </div>
    </div>


  // 2. level of abstraction
  // **************************

  @dom
  private lazy val previewContent: Binding[HTMLElement] =
    <div class="ui grid">
      {for (elem <- UIFormStore.uiState.formElements) yield element(elem).bind}
    </div>

  @dom
  private def element(uiElemVar: Var[UIFormElem]): Binding[HTMLElement] = {
    val uiElem = uiElemVar.bind
    <div class={s"${uiElem.wideClass} wide column"}>
      {BaseElementDiv(uiElem).bind}
    </div>
  }

}
