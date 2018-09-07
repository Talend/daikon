package org.talend.common.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, ReceiveTimeout, Stash }
import akka.cluster.sharding.ShardRegion.Passivate
import akka.pattern.pipe
import com.typesafe.config.Config
import org.talend.common.configuration.ConfigurationService

import scala.concurrent.{ ExecutionContext, Future }

object MultiTenantEntitySupervisor {

  /**
    * Supervisor's nternal messages
    */
  sealed trait ConfigurationMessage

  case class ConfigurationSuccess(originator: ActorRef, command: Any, config: Config) extends ConfigurationMessage

  case class ConfigurationFailure(originator: ActorRef, command: Any, reason: String) extends ConfigurationMessage

  case object StopCommand

}

/**
  * Main entry point of the API: a multi-tenant entity supervisor.
  *
  * On any input command, this supervisor will
  * - extract the current tenant identifier from the command
  * - retrieve this tenant-specific configuration from a provided [[ConfigurationService]] implementation
  * - build the delegate actor's props - including akka persistence configuration and forward the original command to this actor
  *
  * @tparam TENANT_ID the tenant identifier type
  */
trait MultiTenantEntitySupervisor[TENANT_ID] extends ActorLogging {
  this: Actor with Stash with ActorLogging =>

  import MultiTenantEntitySupervisor._

  /**
    * @return the application name - used to identify the current application's tenant-specific configuration
    */
  def application: String

  /**
    * Extracts the current tenant identifier from the provided command
    * @param command the command containing a tenant id
    * @return the tenant id
    */
  def extractTenantIdFrom(command: Any): TENANT_ID

  /**
    * @return the service in charge of fetching tenant specific configuration
    */
  def configurationService: ConfigurationService[TENANT_ID]


  /**
    * Builds the delegate actor's properties
    *
    * @param command the original command
    * @param config the tenant specific configuration retrieved from [[ConfigurationService]]
    * @return the actor's props
    */
  def childProps(command: Any, config: Config): Props

  /**
    * Defines how configuration retrieval errors are handled
    *
    * @param originator the calling actor
    * @param command the original command
    * @param reason the failing reason
    */
  def handleError(originator: ActorRef, command: Any, reason: String): Unit

  implicit val ec: ExecutionContext

  override def receive: Receive = waitingForInitialMessage

  def waitingForInitialMessage: Receive = {
    case command =>
      fetchConfiguration(sender(), command) pipeTo self
      context become waitingForConfiguration
  }

  def waitingForConfiguration: Receive = {
    case ConfigurationSuccess(originator, command, config) =>
      unstashAll()
      val child = context.actorOf(childProps(command, config))
      child.tell(command, originator)
      context become configured(child)
    case ConfigurationFailure(originator, command, reason) =>
      handleError(originator, command, reason)
      context.parent ! Passivate(StopCommand)
    case StopCommand =>
      context stop self
    case _ =>
      stash()
  }

  def configured(child: ActorRef): Receive = {
    case ReceiveTimeout =>
      context.parent ! Passivate(StopCommand)
    case StopCommand =>
      context stop self
    case command =>
      child forward command
  }

  def fetchConfiguration(originator: ActorRef, command: Any): Future[ConfigurationMessage] = {
    val tenantId = extractTenantIdFrom(command)
    log.debug("fetch configuration for tenant {}", tenantId)
    configurationService.forTenant(tenantId, application) map {
      case Right(config) =>
        ConfigurationSuccess(originator, command, config)
      case Left(reason) =>
        log.error("could not process command {}; tenant-specific configuration could not be retrieved because {}", command, reason)
        ConfigurationFailure(originator, command, reason)
    } recover {
      case exception =>
        log.error(exception, "could not process command {}; tenant-specific configuration could not be retrieved", command)
        ConfigurationFailure(originator, command, exception.getMessage)
    }
  }

}
