package org.talend.common.configuration

import javax.inject.Inject

import com.typesafe.config.Config

import scala.concurrent.{ ExecutionContext, Future }

class DefaultConfigurationService[TENANT_ID] @Inject() (
  platform: PlatformConfigurationServiceClient[TENANT_ID],
  builder:  ConfigurationBuilder
)(
  implicit
  ec: ExecutionContext
)
  extends ConfigurationService[TENANT_ID] {

  override def forTenant(tenantId: TENANT_ID, application: String): Future[Either[String, Config]] =
    platform.fetchConfiguration(tenantId, application) map {
      _.right flatMap {
        json => builder.from(json, application)
      }
    }

}
