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

import play.api.mvc.Session
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}

class SessionManagerSpec extends TestSupport with TestData {
  val sessionManager: SessionManager = inject[SessionManager]
  val journeyId = "1234"
  private val session: Session = Session()
  private val testKey = "key"
  private val testValue = "value"

  "SessionManager" must {
    "set a journey id" in {
      sessionManager.getSessionValue(sessionManager.setJourneyId(session, journeyId), "NGR-JourneyId") mustBe Some(journeyId)
    }

    "delete a key" in {
      var s = sessionManager.updateSession(session, testKey, testValue)
      sessionManager.getSessionValue(s, testKey) mustBe Some(testValue)
      s = sessionManager.removeSessionKey(s, testKey)
      sessionManager.getSessionValue(s, testKey) mustBe None
    }
  }

}
