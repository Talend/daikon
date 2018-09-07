package org.talend.common.configuration

import com.typesafe.config.{ Config, ConfigFactory, ConfigValueFactory }
import play.api.libs.json.{ JsObject, JsValue }

class ConfigurationBuilder {

  def from(json: JsValue, application: String): Either[String, Config] =
    for {
      properties <- readApplicationProperties(json, application).right
      config <- readConfig(properties).right
    } yield config

  def readApplicationProperties(json: JsValue, application: String): Either[String, JsObject] =
    (json \ "applications" \ application \ "properties").asOpt[JsObject]
      .toRight(s"missing configuration properties for application $application")

  def readConfig(json: JsObject): Either[String, Config] =
    json.fields.foldLeft[Either[String, Config]](Right(ConfigFactory.empty())) {
      case (error @ Left(_), _)           => error
      case (Right(config), (name, value)) => combine(config, name, value)
    }

  def combine(config: Config, name: String, value: JsValue): Either[String, Config] =
    for {
      value <- readPropertyValue(name, value).right
    } yield config.withValue(name.replace('@', '.'), ConfigValueFactory.fromAnyRef(value))

  def readPropertyValue(name: String, property: JsValue): Either[String, String] =
    (property \ "value").asOpt[String]
      .toRight(s"missing required 'value' attribute for the $name property")

}
