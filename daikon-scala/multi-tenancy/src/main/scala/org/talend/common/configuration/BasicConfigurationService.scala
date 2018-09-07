package org.talend.common.configuration

import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Configuration

import scala.concurrent.Future

class BasicConfigurationService[TENANT_ID] @Inject() (
  configuration: Configuration
) extends ConfigurationService[TENANT_ID] {

  override def forTenant(tenantId: TENANT_ID, application: String): Future[Either[String, Config]] =
    Future.successful(Right(configuration.underlying))

}
