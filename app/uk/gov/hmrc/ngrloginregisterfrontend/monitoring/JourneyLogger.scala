/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrloginregisterfrontend.monitoring

import play.api.Logger
import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.http.CookieNames
import uk.gov.hmrc.ngrloginregisterfrontend.requests.RequestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.requests.JourneyRequest
import uk.gov.hmrc.play.http.HeaderCarrierConverter

object JourneyLogger {

  private val log: Logger = Logger("journey")

  def debug(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Debug)

  def info(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Info)

  def warn(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Warn)

  def error(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Error)

  def debug(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Debug)

  def info(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Info)

  def warn(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Warn)

  def error(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Error)

  private def journeyId(implicit request: JourneyRequest[_]) = s"[journeyId: ${request.journey._id.toString}]"

  //private def traceId(implicit request: JourneyRequest[_]) = s"[traceId: ${request.journey.traceId.toString}]"

  //private def origin(implicit request: JourneyRequest[_]) = s"[origin: ${request.journey.origin.toString}]"

  private def authTokenIsSet(implicit request: JourneyRequest[_]) =
    s"[authTokenIsSet: ${request.session.data.contains("authToken").toString}]"

  private def sessionId(implicit r: RequestHeader) = {
    val hc = r match {
      case r: Request[_] => RequestSupport.hc(r)
      case r             => HeaderCarrierConverter.fromRequest(r)
    }
    s"[sessionId: ${hc.sessionId.map(_.value).getOrElse("")}]"
  }

  private def referer(implicit r: RequestHeader) = s"[Referer: ${r.headers.headers.find(_._1 == "Referer").map(_._2).getOrElse("")}]"

  private def deviceId(implicit r: RequestHeader) = s"[deviceId: ${r.cookies.find(_.name == CookieNames.deviceID).map(_.value).getOrElse("")}]"

  private def context(implicit r: RequestHeader) = s"[context: ${r.method} ${r.path}]] $referer $sessionId $deviceId"

  def makeRichMessage(message: String)(implicit request: RequestHeader): String = request match {
    case r: JourneyRequest[_] =>
      implicit val journeyRequest: JourneyRequest[_] = r
      s"$message $journeyId $authTokenIsSet  $context"
    case r =>
      s"$message $context"
  }

  private sealed trait LogLevel

  private case object Debug extends LogLevel

  private case object Info extends LogLevel

  private case object Warn extends LogLevel

  private case object Error extends LogLevel

  private def logMessage(message: => String, level: LogLevel)(implicit request: RequestHeader): Unit = {
    lazy val richMessage = makeRichMessage(message)
    level match {
      case Debug => log.debug(richMessage)
      case Info  => log.info(richMessage)
      case Warn  => log.warn(richMessage)
      case Error => log.error(richMessage)
    }
  }

  private def logMessage(message: => String, ex: Throwable, level: LogLevel)(implicit request: RequestHeader): Unit = {
    lazy val richMessage = makeRichMessage(message)
    level match {
      case Debug => log.debug(richMessage, ex)
      case Info  => log.info(richMessage, ex)
      case Warn  => log.warn(richMessage, ex)
      case Error => log.error(richMessage, ex)
    }
  }

}
