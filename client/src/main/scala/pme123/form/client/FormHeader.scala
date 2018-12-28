package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.dom.window
import pme123.form.client.data.{DataServices, DataView, UIDataStore}
import pme123.form.client.mapping.MappingView
import pme123.form.client.services.{ClientUtils, I18n, Messages, UIStore}
import pme123.form.shared.services.Language

import scala.language.implicitConversions

private[client] object FormHeader
  extends ClientUtils {

  private val changeForm: Var[Option[String]] = Var(None)

  // 1. level of abstraction
  // **************************
  @dom
  private[client] def create(): Binding[HTMLElement] = {
    <div class="ui main borderless menu">
      {faviconElem.bind}{//
      title.bind //
      }{spacer.bind}<div class="ui right item">
      {//
      formsButton.bind}{//
      editPreviewSwitch.bind}{//
      menuButton.bind}{//
      languageButton.bind}{//
      logInButton.bind}
    </div>{changeFormDiv.bind}
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
  private def editPreviewSwitch = {
    val mainView = UIRoute.route.state.bind
    mainView match {
      case FormEditorView =>
        <button class="ui circular blue icon button"
                data:data-tooltip="Show Form Preview"
                data:data-position="bottom right"
                onclick={_: Event =>
                  UIRoute.changeRoute(FormPreviewView)}>
          <i class="file alternate outline icon"></i>
        </button>
      case FormPreviewView =>
        <button class="ui circular blue icon button"
                data:data-tooltip="Edit Form Elements"
                data:data-position="bottom right"
                onclick={_: Event =>
                  UIRoute.changeRoute(FormEditorView)}>
          <i class="edit icon"></i>
        </button>
      case _ =>
          <span/>
    }

  }


  @dom
  private def formsButton: Binding[HTMLElement] = {
    val mainView = UIRoute.route.state.bind
    <div>
      {mainView match {
      case DataView =>
        DataServices.idents().bind
        identDropdown("Choose Data", UIDataStore.uiState.idents).bind

      case _ =>
        FormServices.idents().bind
        identDropdown("Choose Form", UIFormStore.uiState.idents).bind
    }}
    </div>
  }

  @dom
  private def identDropdown(label: String, idents: Vars[String]): Binding[HTMLElement] =
    <div class="ui left top pointing dropdown icon button">
      <span class="text">
        {label}
      </span>
      <div class="menu">
        {for (ident <- idents) yield identLink(ident).bind}
      </div>
    </div>

  @dom
  private def identLink(ident: String): Binding[HTMLElement] = {
    <a class="item"
       onclick={_: Event =>
         changeForm.value = Some(ident)}>
      {ident}
    </a>
  }

  @dom
  private def logInButton = {
    val user = UIStore.uiState.loggedInUser.bind
    if (user.isDefined)
      <div class="ui left top pointing dropdown icon button">
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
        {FormServices.loggedInUser().bind //
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

    val language = UIStore.activeLanguage.value

    <div class="ui icon top left pointing dropdown icon button">
      <input type="hidden"
             id="languageId"
             value={language.entryName}
             onchange={_: Event =>
               UIStore.changeLanguage(languageId.value)}/>
      <div class="default text">
        <i class={s"${language.flag} big flag"}></i>
      </div>
      <div class="menu">
        {Constants(UIStore.supportedLangs.map(languageItem): _*).map(_.bind)}
      </div>
    </div>
  }

  @dom
  private def languageItem(lang: Language) =
    <div class="item" data:data-value={lang.abbreviation}>
      <i class={s"${lang.flag} big flag"}></i>
    </div>


  @dom
  private lazy val changeFormDiv = {
    val change = changeForm.bind
    if (change.nonEmpty)
      <div>
        {changeForm.value = None
      UIRoute.route.state.value match {
        case DataView =>
          DataServices.getData(change.get).bind
        case _ =>
          FormServices.getForm(change.get).bind
      }}
      </div>
    else
        <span/>
  }

  @dom
  private def menuButton = {
    <div class="ui pointing top left dropdown item">
      <i class="big bars icon"></i>
      <div class="menu">
        {Constants(Seq(FormPreviewView, FormEditorView, DataView, MappingView).map(menuLink): _*).map(_.bind)}
      </div>
    </div>
  }

  @dom
  private def menuLink(view: MainView) = {
    val lang = UIStore.uiState.activeLanguage.bind
    <a class="item"
       href={s"#${view.link}"}>
      <i class={s"${view.icon} icon"}></i>{I18n(lang, s"menu.view.${view.link}")}
    </a>
  }

}
