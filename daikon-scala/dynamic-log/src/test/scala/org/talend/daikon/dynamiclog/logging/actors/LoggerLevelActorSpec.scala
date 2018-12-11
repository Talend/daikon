package org.talend.daikon.dynamiclog.logging.actors

import java.util.concurrent.TimeUnit

import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.pattern.AskSupport
import akka.actor.ActorSystem
import akka.testkit.{TestKitBase, TestProbe}
import akka.util.Timeout
import ch.qos.logback.classic
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.AfterAll
import org.talend.daikon.dynamiclog.logging.actors.LoggerLevelActor._
import play.api.test.WithApplication
import scala.concurrent.duration.FiniteDuration
import play.api.{Application, Configuration, Logger}
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LoggerLevelActorSpec
  extends TestKitBase
    with SpecificationLike
    with AfterAll
    with AskSupport {

  sequential

  override def afterAll {
    shutdown()
  }

  val awaitTimeout: Long = 10
  val awaitTimeoutDuration = FiniteDuration(awaitTimeout, TimeUnit.SECONDS)
  implicit val akkaTimeout: Timeout = Timeout(awaitTimeoutDuration)

  def getDefaultConfigMap: Map[String, Any] = Map[String, Any](
    "bridge-to-actor.long_timeout_sec" -> 5,
    "stream.submitting.timeout.sec" -> 5,
    "stream.killing.timeout.sec" -> 5
  )

  implicit lazy val system: ActorSystem = ActorSystem("DefaultContext")

  lazy val application: Application = new GuiceApplicationBuilder()
    .configure(getDefaultConfigMap)
    .build()

  val configuration: Configuration = application.injector.instanceOf[Configuration]

  new WithApplication(application) {
    "When receiving an UpdateLogLevel message, the LoggerLevelActor" should {
      "change the level of the given logger name" in {
        val distributedPubSubMediator: TestProbe = new TestProbe(system)
        val loggerlevelActor = system.actorOf(LoggerLevelActor.props(distributedPubSubMediator.ref, configuration))
        distributedPubSubMediator.expectMsgType[Subscribe](awaitTimeoutDuration)
        distributedPubSubMediator.reply(SubscribeAck(None.orNull))

        // Simulate that the logger is currently at INFO level
        setCurrentLevel("foo.bar", "info")
        val originalLoggerLevel = getCurrentLevel("foo.bar")

        // Change the log level
        loggerlevelActor ! UpdateLoggerLevel("foo.bar", "debug")
        // Since the actor does not send any response, wait for few millis for the log level change to be active
        val sleepTime = 100
        Thread.sleep(sleepTime)

        // Assert that the level has been changed as expected
        originalLoggerLevel mustEqual Level.INFO
        getCurrentLevel("foo.bar") mustEqual Level.DEBUG
      }
    }
  }

  private def getCurrentLevel(name: String): Level = {
    val logger: classic.Logger = LoggerFactory.getLogger(name).asInstanceOf[classic.Logger]
    lazy val rootLogger: classic.Logger =
      LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[classic.Logger]
    Option(logger.getLevel).fold(rootLogger.getLevel)(identity)
  }

  private def setCurrentLevel(name: String, loggerLevel: LoggerLevel): Unit = {
    val logger: classic.Logger = LoggerFactory.getLogger(name).asInstanceOf[classic.Logger]
    logger.setLevel(loggerLevel)
  }

}
