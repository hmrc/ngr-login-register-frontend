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
import play.api.http.Status.OK
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ErrorResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView

import scala.concurrent.Future

class ConfirmContactDetailsControllerSpec extends ControllerSpecSupport {
  lazy val view: ConfirmContactDetailsView = inject[ConfirmContactDetailsView]
  lazy val citizenDetailsConnector: CitizenDetailsConnector = inject[CitizenDetailsConnector]
  val noNinoAuth: AuthenticatedUserRequest[AnyContentAsEmpty.type] = AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, None, nino = Nino(hasNino = false, None))
  lazy val mockCitizenDetailsConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  def controller() =
    new ConfirmContactDetailsController(
      view = view, authenticate = mockAuthJourney, mcc = mcc, citizenDetailsConnector = citizenDetailsConnector
    )

  def failController() =
    new ConfirmContactDetailsController(
      view = view, authenticate = mockAuthJourney, mcc = mcc, citizenDetailsConnector = mockCitizenDetailsConnector
    )

  "Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

    "return OK and the correct view for a GEffT" in {
      val result = controller().show()(noNinoAuth)
      status(result) mustBe OK
    }

    "person details returns error status" in {
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future(Left(ErrorResponse(any(), any()))))
      val result = failController().show()(authenticatedFakeRequest)
      status(result) mustBe 0
    }

  }

}
