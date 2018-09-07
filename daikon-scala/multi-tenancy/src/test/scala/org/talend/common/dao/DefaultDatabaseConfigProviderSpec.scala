package org.talend.common.dao

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.talend.common.configuration.ConfigurationService
import play.api.Configuration
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class DefaultDatabaseConfigProviderSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  "The DefaultDatabaseConfigProvider" should {
    "delegate to configuration service" in {

      val configService = mock[ConfigurationService[String]]

      val config = Configuration.empty

      val tenantId = "TENANT"

      val application = "APPLICATION"

      val provider = new DefaultDatabaseConfigProvider[String](configService, config)

      val tenantConfig = ConfigFactory.parseString(
        """
          |database {
          |   driver = "slick.driver.H2Driver$"
          |   db {
          |    connectionPool = disabled
          |    driver = "org.h2.Driver"
          |    url = "jdbc:h2:mem:tsql1"
          |  }
          |}
        """.stripMargin)

      configService.forTenant(tenantId, application) returns Future.successful(Right(tenantConfig))

      val result = provider.get(tenantId, application)

      result must beRight[DatabaseConfig[JdbcProfile]].like {
        case databaseConfig => databaseConfig.profileName must be_==("slick.jdbc.H2Profile")

      }.await
    }
    "provide error message in case of config service error" in {

      val configService = mock[ConfigurationService[String]]

      val config = Configuration.empty

      val tenantId = "TENANT"

      val application = "APPLICATION"

      val provider = new DefaultDatabaseConfigProvider[String](configService, config)

      configService.forTenant(tenantId, application) returns Future.successful(Left("This is an error"))

      val result = provider.get(tenantId, application)

      result must beLeft("This is an error").await

    }
  }
}
