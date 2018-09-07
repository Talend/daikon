package org.talend.common.dao

import javax.inject.Inject

import play.api.Logger
import play.api.cache.{ AsyncCacheApi, NamedCache }
import slick.basic.DatabaseConfig
import slick.jdbc.{ JdbcBackend, JdbcProfile }

import scala.concurrent.{ ExecutionContext, Future }

class MultiTenantDatabaseConfigProvider[TENANT_ID] @Inject() (
  databaseConfigProvider:                                        DatabaseConfigProvider[TENANT_ID],
  @NamedCache("tenant-db-cache") dbCache:                        AsyncCacheApi,
  @NamedCache("tenant-sample-storage-cache") sampleStorageCache: AsyncCacheApi
)(implicit ec: ExecutionContext) {

  type Config = DatabaseConfig[JdbcProfile]
  type Block[T] = JdbcBackend#DatabaseDef => Future[T]

  def forTenant[T](tenantId: TENANT_ID, application: String)(
    block: Block[T]
  ): Future[T] = {
    dbCache.get[Config](tenantId.toString) flatMap {
      case None         => dbCacheMiss(tenantId, application, block)
      case Some(config) => dbCacheHit(config, block)
    }
  }

  def getSampleStorage[T](
    tenantId:    TENANT_ID,
    application: String
  ): Future[Either[String, SampleStorageConfig]] = {
    sampleStorageCache.get[SampleStorageConfig](tenantId.toString) flatMap {
      case None               => sampleStorageCacheMiss(tenantId, application)
      case Some(sampleConfig) => sampleStorageCacheHit(sampleConfig)
    }
  }

  private[this] def dbCacheHit[T](
    config: Config,
    block:  Block[T]
  ): Future[T] = {
    Logger.debug("DB Cache hit")
    block(config.db)
  }

  private[this] def dbCacheMiss[T](
    tenantId:    TENANT_ID,
    application: String,
    block:       Block[T]
  ) = {
    Logger.debug("DB Cache miss")
    databaseConfigProvider.get(tenantId, application) flatMap {
      case Right(config) =>
        dbCache.set(tenantId.toString, config) flatMap (_ => block(config.db))
      case Left(error) => Future.failed(new RuntimeException(error))
    }
  }

  private[this] def sampleStorageCacheHit[T](sampleConfig: SampleStorageConfig): Future[Either[String, SampleStorageConfig]] = {
    Logger.debug("Sample storage Cache hit")
    Future(Right(sampleConfig))
  }

  private[this] def sampleStorageCacheMiss[T](
    tenantId:    TENANT_ID,
    application: String
  ): Future[Either[String, SampleStorageConfig]] = {
    Logger.debug("Sample storage Cache miss")
    databaseConfigProvider.getSampleStorageConfig(tenantId).flatMap {
      case Right(config) =>
        sampleStorageCache.set(tenantId.toString, config) flatMap (_ =>
          Future(Right(config)))
      case Left(error) => Future.failed(new RuntimeException(error))
    }
  }

}
