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

package uk.gov.hmrc.ngrloginregisterfrontend.session

import play.api.mvc.{Result, RequestHeader}
import java.util.UUID
import javax.inject.Singleton

@Singleton
class SessionManager {

  val journeyIdKey: String = "NGR-JourneyId"

  private def addToSession(result: Result, key: String, value: String)(implicit requestHeader: RequestHeader) = {
    result.addingToSession(key -> value)
  }

  private def removeFromSession(result: Result, key: String)(implicit requestHeader: RequestHeader) = {
    result.removingFromSession(key)
  }

  def getFromSession(result: Result, key: String)(implicit requestHeader: RequestHeader): Option[String] = {
    result.session.get(key)
  }

  def setJourneyId(result: Result, journeyId: String)(implicit requestHeader: RequestHeader): Result = {
    addToSession(result.withNewSession, journeyIdKey, journeyId)
  }

  def generateJourneyId: String = {
    UUID.randomUUID().toString
  }
}