package org.talend.common.actor

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.{After, Specification}
import org.specs2.runner.JUnitRunner
import org.talend.common.configuration.ConfigurationService
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class MultiTenantEntitySupervisorSpec extends Specification with Mockito {

  val expectedTenant = "EXPECTED_TENANT"

  val tenantWithConfigurationFailure = "FAILURE_TENANT"

  val expectedApplication = ExampleSupervisor.application

  val tenantConfiguration = {
    ConfigFactory.parseString(
      """
        |database {
        |      key1 = value1
        |      key2 = value2
        |}
      """.stripMargin)
  }

  val configService = {
    val configService = mock[ConfigurationService[String]]
    configService.forTenant(any[String], any[String]) returns Future.successful(Left("ConfigurationService error"))
    configService.forTenant(tenantWithConfigurationFailure, expectedApplication) returns Future.failed(new Exception("Bad exception"))
    configService.forTenant(expectedTenant, expectedApplication) returns Future.successful(Right(tenantConfiguration))
    configService
  }

  val configuration = {
    Configuration.from(Map(
      "jdbc-journal.common-key1" -> "common-value1",
      "jdbc-journal.common-key2" -> "common-value2",
      "jdbc-snapshot-store.common-key1" -> "common-value1")
    )
  }

  "MultiTenantEntitySupervisorSpec" should {

    "setup correct plugin for a known tenant" in new AkkaTestkitSpecs2Support() {

      val recorder = mock[Recorder]

      val actor = system.actorOf(ExampleSupervisor.props(configService, configuration, recorder))

      val msg = new MyEntityUpdateMessage(expectedTenant, "testValue")

      actor ! msg

      val msgCaptor = capture[MyEntityUpdateMessage]

      val pluginCaptor = capture[PersistentPluginConfiguration]

      there was afterDuration(3.seconds).one(recorder).onReceive(msgCaptor, pluginCaptor)

      msgCaptor.value must_== msg

      pluginCaptor.value.journalPluginConfig.getString("jdbc-journal-EXPECTED_TENANT.common-key1") must_== "common-value1"
      pluginCaptor.value.journalPluginConfig.getString("jdbc-journal-EXPECTED_TENANT.slick.key1") must_== "value1"
      pluginCaptor.value.journalPluginConfig.getString("jdbc-journal-EXPECTED_TENANT.slick.key2") must_== "value2"
      pluginCaptor.value.snapshotPluginConfig.getString("jdbc-snapshot-store-EXPECTED_TENANT.common-key1") must_== "common-value1"
      pluginCaptor.value.snapshotPluginConfig.getString("jdbc-snapshot-store-EXPECTED_TENANT.slick.key1") must_== "value1"


      there was no(recorder).onConfigurationError(any[String])
    }

    "call error handler on configuration service error" in new AkkaTestkitSpecs2Support() {

      val recorder = mock[Recorder]

      val actor = system.actorOf(ExampleSupervisor.props(configService, configuration, recorder))

      val msg = new MyEntityUpdateMessage("UNKNOWN_TENANT", "testValue")

      actor ! msg

      there was afterDuration(3.seconds).one(recorder).onConfigurationError("ConfigurationService error")

      there was no(recorder).onReceive(any[MyEntityUpdateMessage], any[PersistentPluginConfiguration])
    }

    "call error handler on configuration service failure" in new AkkaTestkitSpecs2Support {

      val recorder = mock[Recorder]

      val actor = system.actorOf(ExampleSupervisor.props(configService, configuration, recorder))

      val msg = new MyEntityUpdateMessage(tenantWithConfigurationFailure, "testValue")

      actor ! msg

      there was afterDuration(3.seconds).one(recorder).onConfigurationError("Bad exception")

      there was no(recorder).onReceive(any[MyEntityUpdateMessage], any[PersistentPluginConfiguration])

    }
  }
}

abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem("MultiTenantEntitySupervisorSpec")) with After {

  // ack to avoid conflict between after and mockito after
  def afterDuration(duration: Duration) = org.specs2.mock.mockito.CalledMatchers.after(duration)

  def after = {
    TestKit.shutdownActorSystem(system)
  }

}
