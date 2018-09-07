package org.talend.common.configuration

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class DefaultConfigurationServiceSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  val tenantId = "463eadf4-8e79-4b51-a205-c73c13767f79"

  class Setup extends Scope {
    val client = mock[PlatformConfigurationServiceClient[String]]
    val builder = mock[ConfigurationBuilder]
    val configurationService = new DefaultConfigurationService(client, builder)
  }

  "The DefaultConfigurationService" should {
    "relay the exception raised by the platform configuration service client" in new Setup {
      client.fetchConfiguration(tenantId, "APP") returns Future.successful(Left("could not contact the platform configuration service, fetch failed"))

      val result = configurationService.forTenant(tenantId, "APP")

      result must beLeft("could not contact the platform configuration service, fetch failed").await
    }

    "relay the error message raised by the configuration builder" in new Setup {
      client.fetchConfiguration(tenantId, "APP") returns Future.successful(Right(Json.obj()))
      builder.from(any[JsValue], any[String]) returns Left("configuration build error")

      val result = configurationService.forTenant(tenantId, "APP")

      result must beLeft("configuration build error").await
    }

    "relay the configuration from the configuration builder" in new Setup {
      client.fetchConfiguration(tenantId, "APP") returns Future.successful(Right(Json.obj()))
      builder.from(Json.obj(), "APP") returns Right(ConfigFactory.empty())

      val result = configurationService.forTenant(tenantId, "APP")

      result must beRight(ConfigFactory.empty()).await
    }
  }

}
