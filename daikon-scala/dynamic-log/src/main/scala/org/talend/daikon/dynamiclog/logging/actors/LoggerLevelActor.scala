package org.talend.daikon.dynamiclog.logging.actors

import java.util.concurrent.TimeUnit

import javax.inject.{Inject, Named}
import akka.actor.{ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.pattern._
import akka.util.Timeout
import ch.qos.logback.classic
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory
import org.talend.dataflow.common.akka.actors.ActorNames._
import org.talend.dataflow.common.akka.actors.UnhandledExceptionLogging
import org.talend.daikon.dynamiclog.logging.actors.LoggerLevelActor._
import play.api.Configuration

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * ==========================
  * Logger level model
  * ==========================
  */
case class LoggerLevel(value: String)

case class LoggerName(value: String)

case class LoggerInfo(name: LoggerName, level: LoggerLevel)

/**
  * ==========================
  * Logger level messages
  * ==========================
  */
case class UpdateLoggerLevel(name: LoggerName, level: LoggerLevel)

/**
  * Actor handling logger level changes.
  * This actor should be unique within a given instance and started during service startup.
  *
  * @param distributedPubSubMediator the [[akka.cluster.pubsub.DistributedPubSubMediator]] actor reference
  * @param configuration             the Play configuration
  */
class LoggerLevelActor @Inject()(
  @Named(DISTRIBUTED_PUB_SUB_MEDIATOR) val distributedPubSubMediator: ActorRef,
  val configuration: Configuration
) extends UnhandledExceptionLogging {

  override def preStart(): Unit = {
    super.preStart()
    log.info("Starting LoggerLevelActor")
    subscribeToMediator()
  }

  override def receive: Receive = {
    case UpdateLoggerLevel(name, level) =>
      val logger: classic.Logger = LoggerFactory.getLogger(name.value).asInstanceOf[classic.Logger]
      lazy val rootLogger: classic.Logger =
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[classic.Logger]
      val originalLevel: Level = Option(logger.getLevel).fold(rootLogger.getLevel)(identity)
      logger.setLevel(level)
      log.info(s"Changed {${name.value}} logger level from $originalLevel to ${logger.getLevel}")

    case other => log.warning(s"Received unexpected message: $other")
  }

  private def subscribeToMediator(): Unit = {
    val timeout: FiniteDuration =
      FiniteDuration(configuration.getOptional[Long]("bridge-to-actor.timeout_sec").getOrElse(DEFAULT_TIMEOUT), TimeUnit.SECONDS)
    implicit val akkaTimeout: Timeout = akka.util.Timeout(timeout)
    val subscribe = distributedPubSubMediator ? Subscribe(LOGGER_LEVEL_TOPIC, self)
    Await.result(subscribe, timeout) match {
      case SubscribeAck(_) =>
        log.info(s"Subscribed to {$LOGGER_LEVEL_TOPIC} topic through DistributedPubSubMediator.")
      case other =>
        log.error(s"Unexpected response after subscribing to {$LOGGER_LEVEL_TOPIC} topic: $other")
    }
  }
}

object LoggerLevelActor {

  implicit def loggerLevelToLogbackLevel(level: LoggerLevel): Level = Level.valueOf(level.value)

  implicit def stringToLoggerName(name: String): LoggerName = LoggerName(name)

  implicit def stringToLoggerLevel(name: String): LoggerLevel = LoggerLevel(name)

  val LOGGER_LEVEL_TOPIC: String = "logger-level"

  val DEFAULT_TIMEOUT : Long = 5

  /**
    * @param distributedPubSubMediator the [[akka.cluster.pubsub.DistributedPubSubMediator]] actor reference
    * @param configuration             the Play configuration
    * @return the relevant [[Props]] to create a [[LoggerLevelActor]]
    */
  def props(distributedPubSubMediator: ActorRef, configuration: Configuration): Props =
    Props(new LoggerLevelActor(distributedPubSubMediator, configuration))

}
