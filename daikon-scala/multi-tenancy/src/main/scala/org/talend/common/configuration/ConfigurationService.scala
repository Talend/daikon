package org.talend.common.configuration

import com.typesafe.config.Config

import scala.concurrent.Future

/**
  * Service responsible to retrieve a tenant configuration for an application.
  *
  * @tparam TENANT_ID the tenant identifier type
  */
trait ConfigurationService[TENANT_ID] {

  /**
    * Retrieves the tenant configuration for an application
    * @param tenantId the tenant identifier
    * @param application the application
    * @return an error message or a configuration
    */
  def forTenant(tenantId: TENANT_ID, application: String): Future[Either[String, Config]]

}
