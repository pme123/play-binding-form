package pme123.form.server.entity

import play.api.libs.json.JsError
import pme123.form.shared.services.SPAException

/**
  * Marker trait for all internal Exceptions, that are handled.
  * Created by pascal.mengelt on 09.08.2016.
  */
case class JsonParseException(error: JsError) extends SPAException {
  val msg: String = error.errors.map(e => s"${e._1}: ${e._2}").mkString(";\n")

}

case class JsonWriteException(msg: String) extends SPAException


case class ServiceException(msg: String, override val cause: Option[Throwable] = None)
  extends SPAException

case class ConfigException(msg: String, override val cause: Option[Throwable] = None)
  extends SPAException



