package org.talend.common.configuration

import org.specs2.concurrent.ExecutionEnv
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.routing.sird
import play.api.routing.sird._
import play.api.test.{PlaySpecification, WsTestClient}
import play.core.server.Server

import scala.concurrent.duration._

class PlatformConfigurationServiceClientITSpec(implicit ee: ExecutionEnv) extends PlaySpecification {

  args(sequential = true)

  def withPlatformConfigurationServiceClient[T](block: PlatformConfigurationServiceClient[String] => T): T =
    Server.withRouterFromComponents() { components => {
      case sird.GET(p"/v1/configurations/accounts/463eadf4-8e79-4b51-a205-c73c13767f79/applications/APP") => components.defaultActionBuilder {
        Results.Ok(Json.obj("configuration" -> "fake"))
      }
      case sird.GET(p"/v1/configurations/accounts/463eadf4-8e79-4b51-a205-c73c13767f79/applications/BAD") => components.defaultActionBuilder {
        Results.BadRequest
      }
      case sird.GET(p"/v1/configurations/accounts/463eadf4-8e79-4b51-a205-c73c13767f79/applications/ERROR") => components.defaultActionBuilder {
        Results.Ok("error")
      }
    }
    } { implicit port =>
      WsTestClient.withClient { ws =>
        val config = Configuration.from(Map(
          "tpsvc.config.url" -> "",
          "tpsvc.config.timeout" -> "20 seconds"
        ))
        block(new PlatformConfigurationServiceClient[String](ws, config))
      }
    }

  "The platform configuration service client" should {
    "return the JSON object fetched from the platform configuration service" in {
      withPlatformConfigurationServiceClient { client =>
        val response = client.fetchConfiguration("463eadf4-8e79-4b51-a205-c73c13767f79", "APP")

        response must beRight[JsValue](Json.obj("configuration" -> "fake")).await
      }
    }

    "return an error message when the platform configuration service returns 400" in {
      withPlatformConfigurationServiceClient { client =>
        val response = client.fetchConfiguration("463eadf4-8e79-4b51-a205-c73c13767f79", "BAD")

        response must beLeft("could not fetch data from platform configuration service, status is 400").await
      }
    }

    "return an error message when the platform configuration service response cannot be parse into JSON" in {
      withPlatformConfigurationServiceClient { client =>
        val response = client.fetchConfiguration("463eadf4-8e79-4b51-a205-c73c13767f79", "ERROR")

        response must beLeft("could not parse JSON from the platform configuration service response").await
      }
    }
  }

}
