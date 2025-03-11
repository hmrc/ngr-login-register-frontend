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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import play.api.mvc.Session
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.Address

class SessionManagerSpec extends TestSupport with TestData {
  val sessionManager: SessionManager = inject[SessionManager]
  val journeyId = "1234"
  private val address = "20, Long Rd, Bournemouth, Dorset, BN110AA, UK"
  val session: Session = Session()
  val testKey = "key"
  val testValue = "value"

  "SessionManager" must {
    "set a journey id" in {
      sessionManager.getSessionValue(sessionManager.setJourneyId(session, journeyId), "NGR-JourneyId") mustBe Some(journeyId)
    }

    "set a address" in {
      sessionManager.getSessionValue(sessionManager.setChosenAddress(session, address), "NGR-ChosenAddressIdKey") mustBe Some(address)
    }

    "set address lookup response" in {
      val addresses = Seq(addressLookupAddress)
      val json = Json.toJson(addresses).toString()
      sessionManager.getSessionValue(sessionManager.setAddressLookupResponse(session, addresses), "Address-Lookup-Response") mustBe Some(json)
    }

    "set postcode" in {
      val postcode = Postcode("W126WA")
      sessionManager.getSessionValue(sessionManager.setPostcode(session, postcode), "Postcode-Key") mustBe Some(postcode.value)
    }

    "delete a key" in {
      var s = sessionManager.updateSession(session, testKey, testValue)
      sessionManager.getSessionValue(s, testKey) mustBe Some(testValue)
      s = sessionManager.removeSessionKey(s, testKey)
      sessionManager.getSessionValue(s, testKey) mustBe None
    }
  }

}
