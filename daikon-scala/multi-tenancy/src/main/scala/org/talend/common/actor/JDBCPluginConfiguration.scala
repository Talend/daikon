package org.talend.common.actor

import com.typesafe.config.{ Config, ConfigFactory }

object JDBCPluginConfiguration {

  val journalPrefix = "jdbc-journal"

  val snapshotPrefix = "jdbc-snapshot-store"

  val tpsvcPrefix = "database"

}

/**
  * A PersistentPluginConfiguration pointing to a JDBC store configured from provided application and tenant-specific
  * configurations provided as parameter
  *
  * @param tenantId the identifier of the current tenant
  * @param tenantConfig tenant-specific configuration
  * @param appConfig application configuration
  * @tparam TENANT_ID tenant identifier type
  */
class JDBCPluginConfiguration[TENANT_ID](tenantId: TENANT_ID, tenantConfig: Config, appConfig: Config) extends PersistentPluginConfiguration {

  import JDBCPluginConfiguration._

  override def journalPluginId: String = s"${journalPrefix}-${tenantId.toString}"

  override def snapshotPluginId: String = s"${snapshotPrefix}-${tenantId.toString}"

  override def journalPluginConfig: Config = pluginConfig(journalPluginId, journalPrefix)

  override def snapshotPluginConfig: Config = pluginConfig(snapshotPluginId, snapshotPrefix)

  def pluginConfig(pluginId: String, path: String): Config =
    ConfigFactory.empty().withValue(pluginId + ".slick", tenantConfig.getValue(tpsvcPrefix))
      .withFallback(ConfigFactory.empty().withValue(pluginId, appConfig.getValue(path)))

}
