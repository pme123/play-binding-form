package pme123.form.shared

import julienrf.json.derived
import play.api.libs.json.OFormat
import pme123.form.shared.services.User

sealed trait FormWebSocketMsg {

}

object FormWebSocketMsg {
  implicit val jsonFormat: OFormat[FormWebSocketMsg] = derived.oformat[FormWebSocketMsg]()

}

// as with akka-http the web-socket connection will be closed when idle for too long.
case object KeepAliveMsg extends FormWebSocketMsg


case class PathMsg(username: User.UserId, route: PathMsg.Message, time: Long = 0)
  extends FormWebSocketMsg

object PathMsg {

  type Message = String

}