package org.talend.daikon.dynamiclog.logging.common

import org.talend.daikon.dynamiclog.logging.actors.{LoggerLevel, LoggerName}
import play.api.mvc.{PathBindable, QueryStringBindable}

object LoggerLevelBindables {

  implicit object LoggerLevelBindable extends QueryStringBindable[LoggerLevel] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LoggerLevel]] = {
      params.get(key).fold[Option[Either[String, LoggerLevel]]](None) {
        _.headOption.map(LoggerLevel) match {
          case Some(name) => Some(Right(name))
          case None       => Some(Left("Unable to bind a LoggerLevel."))
        }
      }
    }

    override def unbind(key: String, value: LoggerLevel): String = value.value

  }

  implicit object LoggerNameBindable extends PathBindable[LoggerName] {

    override def bind(key: String, value: String): Either[String, LoggerName] = Right(LoggerName(value))

    override def unbind(key: String, value: LoggerName): String = value.value

  }

}
