package pme123.form.client.services

import org.scalajs.jquery.{JQuery, jQuery}
import play.api.libs.json.Json
import pme123.form.shared.SemanticForm

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.timers.setTimeout

/**
  *
  */
object SemanticUI {

  // Monkey patching JQuery
  @js.native
  trait SemanticJQuery extends JQuery {
    def dropdown(params: js.Any*): SemanticJQuery = js.native

    def popup(params: js.Any*): SemanticJQuery = js.native

    def modal(params: js.Any*): SemanticJQuery = js.native

    def checkbox(params: js.Any*): SemanticJQuery = js.native

    def form(params: js.Any*): SemanticJQuery = js.native

    def sidebar(params: js.Any*): SemanticJQuery = js.native

  }

  // Monkey patching JQuery with implicit conversion
  implicit def jq2semantic(jq: JQuery): SemanticJQuery = jq.asInstanceOf[SemanticJQuery]

  def initElements(): Unit ={
    setTimeout(100) {
      jQuery(".ui.dropdown")
        .dropdown(js.Dynamic.literal(on = "hover"))
      jQuery(".ui.clearable.dropdown")
        .dropdown(
          js.Dynamic.literal(on = "hover",
            clearable = true)
        )
      jQuery(".ui.checkbox")
        .checkbox()
      jQuery(".ui.tooltip")
        .popup()
    }
  }

  def initForm(form: SemanticForm): Unit ={
    setTimeout(100) {
      jQuery(".ui.form").form(JSON.parse(Json.toJson(form).toString()))
    }
  }

  def columnWide(wide: Int): String = {
    wide match {
      case 1 => "one"
      case 2 => "two"
      case 3 => "three"
      case 4 => "four"
      case 5 => "five"
      case 6 => "six"
      case 7 => "seven"
      case 8 => "eight"
      case 9 => "nine"
      case 10 => "ten"
      case 11 => "eleven"
      case 12 => "twelve"
      case 13 => "thirteen"
      case 14 => "fourteen"
      case 15 => "fifteen"
      case _ => "sixteen"
    }
  }
}