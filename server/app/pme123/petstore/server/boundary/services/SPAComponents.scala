package pme123.form.server.boundary.services

import com.mohiva.play.silhouette.api.Silhouette
import controllers.AssetsFinder
import javax.inject.Inject
import play.Environment
import play.api.mvc._
import pme123.form.server.control.FormConfiguration
import pme123.form.server.control.auth.{DefaultEnv, IdentityService}

import scala.concurrent.ExecutionContext

class SPAComponents @Inject()(val assetsFinder: AssetsFinder,
                              val config: FormConfiguration,
                              val env: Environment,
                              val cc: ControllerComponents,
                              val identityApi: IdentityService,
                              val silhouette: Silhouette[DefaultEnv]
                             )(implicit ec: ExecutionContext) {


}
