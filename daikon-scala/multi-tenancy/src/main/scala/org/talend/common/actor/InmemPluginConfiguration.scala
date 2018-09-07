package org.talend.common.actor

import com.typesafe.config.ConfigFactory

/**
  * An in-memory implementation of PersistentPluginConfiguration
  */
class InmemPluginConfiguration extends PersistentPluginConfiguration {

  override def journalPluginId = "akka.persistence.journal.inmem"

  override def snapshotPluginId = "akka.persistence.snapshot-store.local"

  override def journalPluginConfig = ConfigFactory.empty()

  override def snapshotPluginConfig = ConfigFactory.empty()

}
