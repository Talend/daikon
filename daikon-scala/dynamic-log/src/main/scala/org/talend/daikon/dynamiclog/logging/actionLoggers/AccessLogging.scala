package org.talend.daikon.dynamiclog.logging.actionLoggers

import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * A Logging Action that logs each request and writes it to the "AccessLogger". Useful for logging access actions.
  * log message format : "METHOD URI REMOTE-ADDRESS"
  */
trait AccessLogging {

  val accessLogger = Logger("AccessLogger")

  val ec: ExecutionContext

  val bodyParser: BodyParser[Any]

  object AccessLoggingAction extends ActionBuilder[Request, Any] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      accessLogger.info(s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}")
      block(request)
    }

    override def parser: BodyParser[Any] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

}
