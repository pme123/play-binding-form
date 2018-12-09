package pme123.form.server.boundary.services

import javax.inject._
import jsmessages.JsMessagesFactory
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class HomeController @Inject()(template: views.html.index
                               , val spaComps: SPAComponents
                               , jsMessagesFactory: JsMessagesFactory
                              )
                              (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  private lazy val messages = jsMessagesFactory.all

  def index(): Action[AnyContent] = SecuredAction.async { implicit request: Request[AnyContent] =>
    // uses the AssetsFinder API
    pageConfig(None)
      .map(pc => Ok(template(pc)))
  }


  def i18nMessages(): Action[AnyContent] = SecuredAction { implicit request =>
    Ok(messages(Some("window.Messages")))
  }

}

