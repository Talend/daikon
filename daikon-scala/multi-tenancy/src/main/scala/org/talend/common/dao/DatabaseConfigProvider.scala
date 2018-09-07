package org.talend.common.dao

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait DatabaseConfigProvider[TENANT_ID] {

  def get(tenantId: TENANT_ID, application: String): Future[Either[String, DatabaseConfig[JdbcProfile]]]

  def getSampleStorageConfig(tenantId: TENANT_ID): Future[Either[String, SampleStorageConfig]]

}
