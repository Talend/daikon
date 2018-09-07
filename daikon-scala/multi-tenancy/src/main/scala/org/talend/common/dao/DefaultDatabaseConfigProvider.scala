package org.talend.common.dao

import javax.inject.Inject

import com.typesafe.config.Config
import org.talend.common.configuration.ConfigurationService
import play.api.Configuration
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

class DefaultDatabaseConfigProvider[TENANT_ID] @Inject() (
  configurationService: ConfigurationService[TENANT_ID],
  configuration:        Configuration
)(
  implicit
  ec: ExecutionContext
) extends DatabaseConfigProvider[TENANT_ID] {

  override def get(tenantId: TENANT_ID, application: String): Future[Either[String, DatabaseConfig[JdbcProfile]]] =
    configurationService.forTenant(tenantId, application) map {
      _.right map { config =>
        DatabaseConfig.forConfig("database", config)
      }
    }

  override def getSampleStorageConfig(
    tenantId: TENANT_ID
  ): Future[Either[String, SampleStorageConfig]] = {
    configurationService.forTenant(tenantId, configuration.get[String]("dataset.tpsvc.config.application")) map {
      _.right map {
        config => buildSampleStorageConfig(tenantId, config)
      }
    }
  }

  private def buildSampleStorageConfig(tenantId: TENANT_ID, config: Config) = {

    if (config.hasPath("sample_storage_type")) {
      config.getString("sample_storage_type") match {
        case "S3" => {
          val bucketName = config.getString("s3_bucket_name")
          val appName = "dataset" // FIXME remove when provisioning svc will init config service with the full path
          val tenant = tenantId.toString // FIXME when provisioning service will be configured : config.getString("s3_tenant_prefix")

          if (config.hasPath("s3_region") && config.hasPath("s3_aws_access_key") && config
            .hasPath("s3_aws_secret") && config.hasPath("s3_endpoint")) {
            val region = config.getString("s3_region")
            val accessKey = config.getString("s3_aws_access_key")
            val secret = config.getString("s3_aws_secret")
            val endpoint = config.getString("s3_endpoint")
            val signingRegion = config.getString("s3_signing_region")
            S3SampleStorageConfigBasicAuth(
              region,
              accessKey,
              secret,
              bucketName,
              s"$appName/$tenant",
              Some(endpoint),
              Some(signingRegion)
            )
          } else if (config.hasPath("s3_region") && config.hasPath("s3_aws_access_key") && config
            .hasPath("s3_aws_secret")) {
            val region = config.getString("s3_region")
            val accessKey = config.getString("s3_aws_access_key")
            val secret = config.getString("s3_aws_secret")
            S3SampleStorageConfigBasicAuth(
              region,
              accessKey,
              secret,
              bucketName,
              s"$appName/$tenant",
              None,
              None
            )
          } else {
            S3SampleStorageConfigEC2Auth(
              bucketName,
              s"$appName/$tenant"
            )
          }
        }
        case "DB" =>
          DBSampleStorageConfig
      }
    } else {
      DBSampleStorageConfig
    }
  }
}
