package pme123.form.server.entity

import java.time.{Instant, ZoneId}

import configs.{Configs, Result}
import play.api.Configuration
import pme123.form.shared.services.{Logging, SettingsProp}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
  * created by pascal.mengelt
  * This config uses the small framework typesafe-config.
  * See here the explanation: https://github.com/typesafehub/config
  * The configuration can be overridden direct in distribution.
  * see here the base: http://stackoverflow.com/questions/6201349/how-to-read-properties-file-placed-outside-war
  *
  * The defaults are described in reference.conf
  */
object FormConfSettings {
  val configPath = "pme123.form"

  // env
  val runModeProp = "run.mode"
  val charEncodingProp = "char.encoding"
  val timezoneProp = "timezone"
  val wsocketHostsAllowedProp = "wsocket.hosts.allowed"
  val servicesProp = "services"

  // security
  val authenticatorExpiryProp = "security.cookieAuthenticator.rememberMe.authenticatorExpiry"
  val authenticatorIdleTimeoutProp = "security.cookieAuthenticator.rememberMe.authenticatorIdleTimeout"
  val cookieMaxAgeProp = "security.cookieAuthenticator.rememberMe.cookieMaxAge"

  // other
  val httpContextProp = "play.http.context"

}

// this settings will be validated on startup
abstract class FormConfSettings(config: Configuration)
  extends SettingsPropsImplicits {

  import FormConfSettings._

  val name = "form"

  private val baseConfig: Configuration = config.get[Configuration](configPath)

  // env
  val runMode: String = baseConfig.get[String](runModeProp)
  val charEncoding: String = baseConfig.get[String](charEncodingProp)
  val timezone: String = baseConfig.get[String](timezoneProp)
  val timezoneID: ZoneId = ZoneId.of(timezone)
  val wsocketHostsAllowed: Seq[String] = baseConfig.get[Seq[String]](wsocketHostsAllowedProp)
  // services
  val services: Result[Seq[ServiceConfig]] = Configs[Seq[ServiceConfig]].get(baseConfig.underlying, servicesProp)
  // db
  val dbDriver: String = config.get[String]("db.default.driver")
  val dbUrl: String = config.get[String]("db.default.url")
  val dbUsername: String = config.get[String]("db.default.username")
  val dbPassword: String = config.get[String]("db.default.password")

  //security
  val authenticatorExpiry: FiniteDuration = config.get[FiniteDuration](authenticatorExpiryProp)
  val authenticatorIdleTimeout: Option[FiniteDuration] = config.getOptional[FiniteDuration](authenticatorIdleTimeoutProp)
  val cookieMaxAge: Option[FiniteDuration] = config.getOptional[FiniteDuration](cookieMaxAgeProp)

  // other
  val httpContext: String = config.get[String](httpContextProp)

  val serviceMap: Map[String, ServiceConfig] = services.value.map(s => s.ident -> s).toMap

  lazy val props: Seq[SettingsProp] = {
    Seq(
      //api
      SettingsProp(runModeProp, runMode)
      , SettingsProp(timezoneProp, timezone)
      , SettingsProp(charEncodingProp, charEncoding)
      , SettingsProp(wsocketHostsAllowedProp, wsocketHostsAllowed.map(_.toString))
      , SettingsProp(servicesProp, services.toString)
      // security
      , SettingsProp(authenticatorExpiryProp, authenticatorExpiry)
      , SettingsProp(authenticatorIdleTimeoutProp, authenticatorIdleTimeout)
      , SettingsProp(cookieMaxAgeProp, cookieMaxAge)
      // other
      , SettingsProp(httpContextProp, httpContext)
    )
  }
}


trait SettingsPropsImplicits
  extends Logging {
  def name: String

  def props: Seq[SettingsProp]

  protected def pwd(value: String): String = "*" * value.length

  lazy val asString: String = props.map(_.asString(name)).mkString("\n")

  implicit def fromAny(bool: Boolean): String = bool.toString

  implicit def fromInt(nbr: Int): String = nbr.toString

  implicit def fromInstant(inst: Instant): String = inst.toString

  implicit def fromOption(opt: Option[_]): String = opt.map(_.toString).getOrElse("-")

  implicit def fromFiniteDuration(dur: FiniteDuration): String = dur.toString

  implicit def fromSeq(seq: Seq[String]): String = seq.mkString("[", ",", "]")

}