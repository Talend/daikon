package org.talend.daikon.dynamic.logging

import play.api.Logger

/**
  * Common [[Logger]] for the application services.
  * Mix in this trait to access the logger instance.
  */
trait Logging {

  lazy val logger = Logger(getClass)

}
