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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ProvideTRNView
import scala.concurrent.Future

class ProvideTRNControllerSpec extends ControllerSpecSupport with TestData {

  lazy val view: ProvideTRNView = inject[ProvideTRNView]
  lazy val mockCIDConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  def controller() =
    new ProvideTRNController(
      mockCIDConnector, view = view, authenticate = mockAuthJourney, mcc = mcc
    )

  "ProvideTRNController" must {
    "return OK and the correct view for a GET" in {
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

    "Calling the submit function return a 303 and the correct redirect location" in {
      when(mockCIDConnector.getMatchingResponse(any())(any())).thenReturn(Future.successful(Right(matchingDetailsResponse)))
      val result = controller().submit()(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ConfirmUTRController.show.url)
    }
  }


}
