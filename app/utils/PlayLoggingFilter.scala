package utils

import java.util.UUID

import com.codahale.metrics.Meter
import models.audit.RequestLog
import models.database.queries.RequestLogQueries
import org.joda.time.LocalDateTime
import play.api.http.Status
import play.api.mvc._
import services.database.Database
import utils.metrics.Instrumented
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object PlayLoggingFilter extends Filter with Logging with Instrumented {
  val prefix = "solitaire-gg.requests."

  val knownStatuses = Seq(
    Status.OK, Status.BAD_REQUEST, Status.FORBIDDEN, Status.NOT_FOUND,
    Status.CREATED, Status.TEMPORARY_REDIRECT, Status.INTERNAL_SERVER_ERROR, Status.CONFLICT,
    Status.UNAUTHORIZED, Status.NOT_MODIFIED
  )

  lazy val statusCodes: Map[Int, Meter] = knownStatuses.map(s => s -> metricRegistry.meter(prefix + s.toString)).toMap

  lazy val requestsTimer = metricRegistry.timer(prefix + "requestTimer")
  lazy val activeRequests = metricRegistry.counter(prefix + "activeRequests")
  lazy val otherStatuses = metricRegistry.meter(prefix + "other")

  def apply(nextFilter: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis
    val context = requestsTimer.time()
    activeRequests.inc()

    def logCompleted(request: RequestHeader, result: Result): Unit = {
      activeRequests.dec()
      context.stop()
      statusCodes.getOrElse(result.header.status, otherStatuses).mark()
    }

    nextFilter(request).transform(
      result => {
        logCompleted(request, result)
        if (request.path.startsWith("/assets")) {
          result
        } else {
          val endTime = System.currentTimeMillis
          val requestTime = endTime - startTime
          log.info(s"${result.header.status} (${requestTime}ms): ${request.method} ${request.uri}")
          result.withHeaders("X-Request-Time-Ms" -> requestTime.toString)
        }
      },
      exception => {
        logCompleted(request, Results.InternalServerError)
        exception
      }
    )
  }
}
