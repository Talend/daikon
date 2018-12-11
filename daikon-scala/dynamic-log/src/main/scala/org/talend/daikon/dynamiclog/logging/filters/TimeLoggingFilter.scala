package org.talend.daikon.dynamiclog.logging.filters

import play.api.Logger
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

/**
  * This filter logs each request's processing time in ms and writes to a log message "TimeLogger".
  * log message format : "METHOD URI TIME STATUS"
  */
abstract class TimeFilter extends Filter {

  val timeLogger = Logger("TimeLogger")

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader)(
    implicit ec: ExecutionContext): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      timeLogger.info(
        s"${requestHeader.method} ${requestHeader.uri} " +
          s"took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

import javax.inject.Inject

import play.api.http.HttpFilters

/**
  * The filter class to use in configuration
  *
  * @param filter
  */
class TimeLoggingFilter @Inject()(filter: TimeFilter) extends HttpFilters {
  val filters = Seq(filter)
}
