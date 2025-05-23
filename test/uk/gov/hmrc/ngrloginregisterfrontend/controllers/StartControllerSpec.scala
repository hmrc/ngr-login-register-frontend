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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.StartView

class StartControllerSpec extends ControllerSpecSupport {
  lazy val startView: StartView = inject[StartView]
  val testUUID = "00ce4ed2-a446-444b-905f-3cc148a1f831"

  def controller = new StartController(startView, mcc, mockSessionManager)


  "Start Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller.show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

    "redirect to confirm contact details when start button pressed" in {
      val result = controller.submit()(authenticatedFakeRequest)
      redirectLocation(result) mustBe Some(routes.ConfirmContactDetailsController.show().url)
      status(result) mustBe 303
    }

    "should store a new journey ID" in {
      when(mockSessionManager.generateJourneyId).thenReturn(testUUID)
      val result = controller.show()(authenticatedFakeRequest)
      result.map(result => {
        mockSessionManager.getSessionValue(result.session, "NGR-JourneyId") mustBe testUUID
      })
    }
  }
}
