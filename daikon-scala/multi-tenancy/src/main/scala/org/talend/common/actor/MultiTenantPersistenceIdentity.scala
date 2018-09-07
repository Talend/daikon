package org.talend.common.actor

import akka.persistence.{PersistenceIdentity, RuntimePluginConfig}

/**
  * Identifies a [[akka.persistence.PersistentActor]] that supports multi-tenancy.
  *
  * It delegates tenant-specific configuration retrieval to the result of [[.plugin]] method
  */
trait MultiTenantPersistenceIdentity extends PersistenceIdentity with RuntimePluginConfig {

  /**
    * @return the akka persistence configuration
    */
  def plugin: PersistentPluginConfiguration

  override def journalPluginId = plugin.journalPluginId

  override def snapshotPluginId = plugin.snapshotPluginId

  override def journalPluginConfig = plugin.journalPluginConfig

  override def snapshotPluginConfig = plugin.snapshotPluginConfig

}
