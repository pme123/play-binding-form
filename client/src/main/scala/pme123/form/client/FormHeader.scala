package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.dom.window
import pme123.form.client.services.{ClientUtils, UIStore}
import pme123.form.shared.services.Language

import scala.language.implicitConversions

private[client] object FormHeader
  extends ClientUtils {

  // 1. level of abstraction
  // **************************
  @dom
  private[client] def create(): Binding[HTMLElement] = {
    <div class="ui main borderless menu">
      {faviconElem.bind}{//
      title.bind //
      }{spacer.bind}<div class="ui right item">
      {//
      logInButton.bind}{//
      languageButton.bind}
    </div>
    </div>

  }

  // 2. level of abstraction
  // **************************

  @dom
  private def title = {
    <div class="item">
      <div class="content">
        <h4 class="header">Form Creator</h4>
        <div class="meta">Play - Binding Demo</div>
      </div>
    </div>
  }

  @dom
  private def spacer = {
    <div class="item">
      <div class="content">
        <h4 class="header">
          &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
        </h4>
        <div class="meta"></div>
      </div>
    </div>
  }

  @dom
  private def logInButton = {
    val user = UIStore.uiState.loggedInUser.bind
    if (user.isDefined)
      <div class="ui floating dropdown icon button">
        <div class="ui mini circular image">
          <img src={staticAsset(user.avatar)}/>
        </div>
        <span class="text">
          &nbsp;{user.fullName}
        </span>
        <div class="menu">
          <a href={s"${UIStore.uiState.webContext.value}/auth/logout"} class="item">Log Out</a>
        </div>
      </div>
    else
      <div class="ui item">
        {ServerServices.loggedInUser().bind //
        }<button class="ui icon button"
                 onclick={_: Event =>
                   window.open(s"${UIStore.uiState.webContext.value}/auth/login", "_self")}
                 data:data-tooltip="Log In"
                 data:data-position="bottom right">
        <i class="sign in alternate icon large"></i>
      </button>
      </div>
  }

  @dom
  private lazy val languageButton = {

    val language = FormUIStore.uiState.activeLanguage.value

      <div class="ui icon top left pointing dropdown icon button">
        <input type="hidden"
               id="languageId"
               value={language.entryName}
               onchange={_: Event =>
                 FormUIStore.changeLanguage(languageId.value)}/>
        <div class="default text">
          <i class={s"${language.flag} big flag"}></i>
        </div>
        <div class="menu">
          {Constants(FormUIStore.supportedLangs.map(languageItem): _*).map(_.bind)}
        </div>
      </div>
  }

  @dom
  private def languageItem(lang: Language) =
    <div class="item" data:data-value={lang.abbreviation}>
      <i class={s"${lang.flag} big flag"}></i>
    </div>

}
