package org.talend.common.actor

import com.typesafe.config.Config

/**
  * An akka persistent actor configuration
  */
trait PersistentPluginConfiguration {

  /**
    * @return Configuration id of the journal plugin servicing the persistent actor.
    *
    * @see [[akka.persistence.PersistenceIdentity.journalPluginId]]
    */
  def journalPluginId: String

  /**
    * @return Configuration id of the snapshot plugin servicing the persistent actor.
    *
    * @see [[akka.persistence.PersistenceIdentity.snapshotPluginId]]
    */
  def snapshotPluginId: String

  /**
    * @return the journal plugin configuration
    */
  def journalPluginConfig: Config

  /**
    * @return the snapshot plugin configuration
    */
  def snapshotPluginConfig: Config

}
