package pme123.form.server.entity

import julienrf.json.derived
import play.api.libs.json.{Json, OFormat}

case class ServiceConfig(ident: String,
                         url: String,
                         mocked: Boolean = false,
                         //auth: ServiceAuth = ServiceAuthNone,
                        )

object ServiceConfig {

  implicit val jsonFormat: OFormat[ServiceConfig] = Json.format[ServiceConfig]
}

sealed trait ServiceAuth {
}

object ServiceAuthNone extends ServiceAuth {
}


case class ServiceAuthBasic
(username: String,
 password: String)
  extends ServiceAuth {
}

object ServiceAuth {
  implicit val jsonFormat: OFormat[ServiceConfig] = derived.oformat[ServiceConfig]()

}


