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

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.Address

import java.util.UUID
import javax.inject.{Inject, Singleton}

@Singleton
class SessionManager @Inject()(mcc: MessagesControllerComponents) {

  private val journeyIdKey: String = "NGR-JourneyId"
  private val addressLookupResponseKey: String = "Address-Lookup-Response"

  def getSessionValue(session: Session, key: String): Option[String] =
    session.get(key)

  def updateSession(session: Session, key: String, value: String): Session =
    session + (key -> value)

  def removeSessionKey(session: Session, key: String): Session =
    session - key

  def setJourneyId(session: Session, journeyId: String): Session = {
    updateSession(session, journeyIdKey, journeyId)
  }

  def setAddressLookupResponse(session: Session, addresses: Seq[Address]): Session = {
    updateSession(session, addressLookupResponseKey, Json.prettyPrint(Json.toJson(addresses)))
  }

  def generateJourneyId: String = {
    UUID.randomUUID().toString
  }
}