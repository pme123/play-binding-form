package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.form.UIFormElem
import pme123.form.client.form.UIFormElem.ChangeEvent
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{ClientUtils, I18n, UIStore}
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared.services.Language.{DE, EN}
import pme123.form.shared.{BaseElement, DataType, ElementTexts}

import scala.util.matching.Regex

trait MainView
  extends ClientUtils {

  def link: String

  def domain: String = link

  def icon: String

  lazy val hashRegex: Regex = s"""#$link""".r

  def create(): Binding[HTMLElement]

  @dom
  protected def header(identVar: Var[String], changeEvent: ChangeEvent, buttons: Binding[HTMLElement]*): Binding[HTMLElement] = {
    val activeLang = UIStore.activeLanguage.bind
    <div class="ui borderless datamenu menu">
      <div class="ui item">
        <h3 class="header">
          <i class={s"$icon icon"}></i> &nbsp; &nbsp;{//
          I18n(activeLang, s"menu.view.$link")}
        </h3>
      </div>
      <div class="ui right item">
        {val ident = identVar.bind
      BaseElementDiv(
        UIFormElem(
          BaseElement(
            s"$link-ident",
            TEXTFIELD,
            DataType.STRING,
            ElementTexts.label(Map(
              DE -> I18n(DE, s"view.$domain.ident"),
              EN -> I18n(EN, s"view.$domain.ident"),
            )),
            value = Some(ident),
            inline = true),
          changeEvent,
        )).bind}&nbsp; &nbsp;{//
        Constants(buttons: _*).map(_.bind)}
      </div>
    </div>
  }
}

