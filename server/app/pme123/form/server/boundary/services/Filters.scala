package pme123.form.server.boundary.services

import java.util.concurrent.TimeUnit

import akka.util.ByteString
import com.codahale.metrics.{JmxReporter, MetricRegistry}
import javax.inject.Inject
import play.api.Logger
import play.api.http.DefaultHttpFilters
import play.api.libs.streams.Accumulator
import play.api.mvc._
import play.filters.cors.CORSFilter
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter
import play.filters.hosts.AllowedHostsFilter

import scala.concurrent.ExecutionContext

/**
  * retrieved from here: https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters#Filters
  * see:  play.http.filters in reference.conf
  * author: pascal
  */
class Filters @Inject()(log: LoggingFilter
                        , error: ErrorHandlingFilter)
  extends DefaultHttpFilters(log, error)

class ProdFilters @Inject()(log: LoggingFilter
                            , error: ErrorHandlingFilter
                            , securityHeadersFilter: SecurityHeadersFilter // see https://www.playframework.com/documentation/2.5.x/SecurityHeaders#configuring-security-headers
                            , corsFilter: CORSFilter // see https://www.playframework.com/documentation/2.5.x/CorsFilter
                            , csrfFilter: CSRFFilter
                            , allowedHostsFilter: AllowedHostsFilter // see https://www.playframework.com/documentation/2.5.x/AllowedHostsFilter
                           )
  extends DefaultHttpFilters(log, error, securityHeadersFilter, corsFilter, csrfFilter, allowedHostsFilter)

class NoFilters @Inject()()
  extends DefaultHttpFilters()

class LoggingFilter @Inject()(registry: MetricRegistry)(implicit ec: ExecutionContext) extends EssentialFilter {
  val logger = Logger("access-filter")

  private lazy val runJmxReporter = JmxReporter
    .forRegistry(registry)
    .inDomain("play-binding-form")
    .convertRatesTo(TimeUnit.MINUTES)
    .build.start()

  private val timer = registry.timer("access-logging")

  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader): Accumulator[ByteString, Result] = {
      runJmxReporter // make sure the reporter is started
      val context = timer.time()
      try {
        val startTime = System.currentTimeMillis

        val accumulator: Accumulator[ByteString, Result] = nextFilter(requestHeader)

        accumulator.map { result =>

          val endTime = System.currentTimeMillis
          val requestTime = endTime - startTime
          logger.info(s"${timer.getCount}. ${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")
          result.withHeaders("Request-Time" -> requestTime.toString)

        }
      } finally context.close()
    }
  }
}

class ErrorHandlingFilter @Inject()(implicit ec: ExecutionContext)
  extends EssentialFilter {
  val logger = Logger("error-filter")

  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader): Accumulator[ByteString, Result] = {

      nextFilter(requestHeader).recover {
        case t: Throwable =>
          logger.error(t.getMessage, t)
          Results.InternalServerError(t.getMessage)
      }(ec)
    }
  }
}
