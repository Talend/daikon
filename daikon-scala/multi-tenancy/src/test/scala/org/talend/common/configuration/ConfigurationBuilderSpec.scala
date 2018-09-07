package org.talend.common.configuration

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class ConfigurationBuilderSpec extends Specification {

  val payloadFromPlatformConfig =
    """{
      |    "accountId": "463eadf4-8e79-4b51-a205-c73c13767f79",
      |    "properties": {},
      |    "applications": {
      |        "APP": {
      |            "applicationId": "APP",
      |            "properties": {
      |                "database@profile": {
      |                    "value": "slick.jdbc.PostgresProfile$",
      |                    "encrypted": false
      |                },
      |                "database@db@driver": {
      |                    "value": "org.postgresql.Driver",
      |                    "encrypted": false
      |                },
      |                "database@db@url": {
      |                    "value": "jdbc:postgresql://postgres:5432/app",
      |                    "encrypted": false
      |                },
      |                "database@db@user": {
      |                    "value": "postgres",
      |                    "encrypted": false
      |                },
      |                "database@db@password": {
      |                    "value": "postgres",
      |                    "encrypted": false
      |                },
      |                "database@db@connectionTestQuery": {
      |                    "value": "SELECT 1",
      |                    "encrypted": false
      |                }
      |            }
      |        }
      |    }
      |}""".stripMargin

  "The config builder" should {
    "return a typesafe config initialized with the data from the JSON payload" in {
      val configOrError = new ConfigurationBuilder().from(Json.parse(payloadFromPlatformConfig), "APP")

      val config = configOrError.right.get
      config.getString("database.profile") === "slick.jdbc.PostgresProfile$"
      config.getString("database.db.driver") === "org.postgresql.Driver"
      config.getString("database.db.url") === "jdbc:postgresql://postgres:5432/app"
      config.getString("database.db.user") === "postgres"
      config.getString("database.db.password") === "postgres"
      config.getString("database.db.connectionTestQuery") === "SELECT 1"
    }

    "return an empty typesafe config when the configuration object is empty" in {
      val configOrError = new ConfigurationBuilder().from(Json.parse("""{"applications":{"APP":{"properties":{}}}}"""), "APP")
      configOrError must beRight(ConfigFactory.empty())
    }

    "return an error when a property misses the 'value' attribute" in {
      val configOrError = new ConfigurationBuilder().from(Json.parse("""{"applications":{"APP":{"properties":{"a@b@c":{"data":"abc"}}}}}"""), "APP")
      configOrError must beLeft("missing required 'value' attribute for the a@b@c property")
    }

    "return an error when there is no properties for the application" in {
      val configOrError = new ConfigurationBuilder().from(Json.parse(payloadFromPlatformConfig), "OTHER")
      configOrError must beLeft("missing configuration properties for application OTHER")
    }

    "return an error when properties is not an object" in {
      val configOrError = new ConfigurationBuilder().from(Json.parse("""{"applications":{"APP":{"properties":null}}}"""), "APP")
      configOrError must beLeft("missing configuration properties for application APP")
    }
  }

}
