package org.talend.common.configuration

import javax.inject.Inject

import play.api.http.{ HeaderNames, MimeTypes, Status }
import play.api.libs.json.JsValue
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{ WSClient, WSResponse }
import play.api.{ Configuration, Logger }

import scala.concurrent.duration.Duration
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

class PlatformConfigurationServiceClient[TENANT_ID] @Inject() (ws: WSClient, configuration: Configuration)(implicit ec: ExecutionContext) {

  val configSvcURL = configuration.get[String]("tpsvc.config.url")
  val requestTimeout = configuration.get[Duration]("tpsvc.config.timeout")

  def fetchConfiguration(tenantId: TENANT_ID, application: String): Future[Either[String, JsValue]] =
    executeRequest(tenantId, application) map toJson recover fromFailure

  private[this] def executeRequest(tenantId: TENANT_ID, application: String): Future[WSResponse] =
    ws.url(s"$configSvcURL/v1/configurations/accounts/${tenantId.toString}/applications/${application}")
      .withRequestFilter(AhcCurlRequestLogger())
      .addHttpHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .withRequestTimeout(requestTimeout)
      .get()

  private[this] def toJson(response: WSResponse): Either[String, JsValue] =
    for {
      _ <- isSuccessful(response.status).right
      json <- extractJson(response).right
    } yield json

  private[this] def isSuccessful(status: Int): Either[String, Unit] =
    Either.cond(Status.isSuccessful(status), (), s"could not fetch data from platform configuration service, status is ${status}")

  private[this] def extractJson(response: WSResponse): Either[String, JsValue] =
    Try(Right(response.json)) getOrElse Left("could not parse JSON from the platform configuration service response")

  private[this] def fromFailure: PartialFunction[Throwable, Either[String, JsValue]] = {
    case exception =>
      val message = "could not communicate with the platform configuration service"
      Logger.error(s"${message} at ${configSvcURL}", exception)
      Left(message)
  }

}
