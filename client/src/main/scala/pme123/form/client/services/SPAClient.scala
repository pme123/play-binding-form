package pme123.form.client.services

import pme123.form.shared.services.Logging
import slogging.{ConsoleLoggerFactory, LoggerConfig}

trait SPAClient
  extends ClientUtils
    with Logging {

  LoggerConfig.factory = ConsoleLoggerFactory()

  def initClient(context: String): Unit = {
    info(s"Init client with Context: $context")
    UIStore.changeWebContext(context)
  }
}
