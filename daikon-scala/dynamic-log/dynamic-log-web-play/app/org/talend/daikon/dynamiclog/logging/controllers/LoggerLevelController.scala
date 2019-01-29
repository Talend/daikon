package org.talend.daikon.dynamiclog.logging.controllers

import javax.inject.{Inject, Named}
import akka.actor.ActorRef
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import ch.qos.logback.classic
import ch.qos.logback.classic.Level
import org.slf4j.{Logger, LoggerFactory}
import org.talend.daikon.dynamiclog.logging.actors.LoggerLevelActor.loggerLevelTopic
import org.talend.daikon.dynamiclog.logging.actors.{LoggerLevel, LoggerName, UpdateLoggerLevel}
import org.talend.daikon.dynamiclog.logging.common.ActorNames._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import org.talend.daikon.dynamiclog.logging.common.LoggerLevelBindables._

class LoggerLevelController @Inject()(
                                       override val controllerComponents: ControllerComponents,
                                       @Named(distributedPubSubMediatorName) val distributedPubSubMediator: ActorRef,
                                       implicit val configuration: Configuration,
                                       implicit val ec: ExecutionContext
) extends BaseController {

  // Play 2.x no longer supports paths with optional parameters
  // (see https://stackoverflow.com/questions/14980952/routes-with-optional-parameter-play-2-1-scala).
  // This is why the most straightforward workaround is not to use Option[T] but to create distinct methods when needed
  // since overloading methods is not allowed as well.

  def updateLoggerLevel(name: LoggerName, level: LoggerLevel): Action[AnyContent] =
    Action.async { implicit request =>
      distributedPubSubMediator ! Publish(loggerLevelTopic, UpdateLoggerLevel(name, level))
      Future(NoContent)
    }

  def updateRootLoggerLevel(level: LoggerLevel): Action[AnyContent] =
    updateLoggerLevel(LoggerName(Logger.ROOT_LOGGER_NAME), level)

  def getLoggerLevel(name: LoggerName): Action[AnyContent] =
    Action.async { implicit request =>
      val logger: classic.Logger = LoggerFactory.getLogger(name.value).asInstanceOf[classic.Logger]
      lazy val rootLogger: classic.Logger =
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[classic.Logger]
      val originalLevel: Level = Option(logger.getLevel).fold(rootLogger.getLevel)(identity)
      Future(Ok(Json.obj("logger" -> Json.obj("name" -> name.value, "level" -> originalLevel.levelStr))))
    }

}
