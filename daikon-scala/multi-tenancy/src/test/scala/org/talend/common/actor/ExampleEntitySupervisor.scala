package org.talend.common.actor

import akka.actor.{Actor, ActorRef, Props, Stash}
import com.typesafe.config.Config
import org.talend.common.configuration.ConfigurationService
import play.api.Configuration

import scala.concurrent.ExecutionContext

case class MyEntityUpdateMessage(tenantId: String, updatedValue: String) {

}

trait Recorder {

  def onReceive(msg: MyEntityUpdateMessage, plugin: PersistentPluginConfiguration) : Unit

  def onConfigurationError(reason: String): Unit

}

object ExampleSupervisor {

  def application = "ExampleApplication"

  def props(configurationService: ConfigurationService[String], configuration: Configuration, recorder: Recorder)
           (implicit ec: ExecutionContext) = {
    Props(new ExampleSupervisor(configurationService, application, configuration, recorder))
  }

}

class ExampleSupervisor(override val configurationService: ConfigurationService[String], override val application: String, val configuration: Configuration, recorder: Recorder)
                       (implicit val ec: ExecutionContext)
  extends Actor with Stash with MultiTenantEntitySupervisor[String] {

  override def extractTenantIdFrom(command: Any): String = {
    command.asInstanceOf[MyEntityUpdateMessage].tenantId
  }

  override def childProps(command: Any, config: Config): Props = {
    val cmd = command.asInstanceOf[MyEntityUpdateMessage]
    val plugin = new JDBCPluginConfiguration(cmd.tenantId, config, configuration.underlying)
    ExampleEntity.props(plugin, recorder)
  }

  override def handleError(originator: ActorRef, command: Any, reason: String): Unit = {
    recorder.onConfigurationError(reason)
  }
}

object ExampleEntity {

  def props(plugin: PersistentPluginConfiguration, recorder: Recorder): Props = {
    Props(new ExampleEntity(plugin, recorder))
  }
}


class ExampleEntity(val plugin: PersistentPluginConfiguration, recorder: Recorder) extends Actor {

  def receive = {
    case msg: MyEntityUpdateMessage => recorder.onReceive(msg, plugin)
  }
}

