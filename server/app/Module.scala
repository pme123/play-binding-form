import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import pme123.form.server.control.FormDBInitializer
import slogging.{LoggerConfig, SLF4JLoggerFactory}

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    // framework
    LoggerConfig.factory = SLF4JLoggerFactory()

    bind(classOf[FormDBInitializer])
      .asEagerSingleton()
  }
}
