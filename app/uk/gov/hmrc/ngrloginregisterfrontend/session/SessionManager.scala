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
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.Address

import java.util.UUID
import javax.inject.{Inject, Singleton}

@Singleton
class SessionManager @Inject()(mcc: MessagesControllerComponents) {

   private val journeyIdKey      : String = "NGR-JourneyId"
   val chosenAddressIdKey: String = "NGR-Chosen-Address-Key"
   val addressLookupResponseKey: String = "Address-Lookup-Response"
   val postcodeKey: String = "Postcode-Key"

  def getSessionValue(session: Session, key: String): Option[String] =
    session.get(key)

  def updateSession(session: Session, key: String, value: String): Session =
    session + (key -> value)

  def removeSessionKey(session: Session, key: String): Session =
    session - key

  def setJourneyId(session: Session, journeyId: String): Session = {
    updateSession(session, journeyIdKey, journeyId)
  }

  def setChosenAddress(session: Session, address: Address): Session = {
    val splitIndex: Int = if (address.lines.size > 2) address.lines.size / 2 else 1
    val lineSeq = address.lines.splitAt(splitIndex)
    val line1 = lineSeq._1.mkString(", ")
    val line2 = if (lineSeq._2.isEmpty) None else Some(lineSeq._2.mkString(", "))
    val ngrAddress = uk.gov.hmrc.ngrloginregisterfrontend.models.Address(
      line1, line2, address.town, None, Postcode(address.postcode), address.country.code)
    updateSession(session, chosenAddressIdKey, Json.toJson(ngrAddress).toString())
  }

  def setAddressLookupResponse(session: Session, addresses: Seq[Address]): Session = {
    updateSession(session, addressLookupResponseKey, Json.toJson(addresses).toString())
  }

  def setPostcode(session: Session, postcode: Postcode): Session = {
    updateSession(session, postcodeKey, postcode.value)
  }

  def generateJourneyId: String = {
    UUID.randomUUID().toString
  }
}