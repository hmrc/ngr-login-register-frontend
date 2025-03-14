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
import play.api.Play.materializer
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Nino, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoView
import uk.gov.hmrc.auth.core.{Nino => authNino}

import scala.concurrent.Future

class NinoControllerSpec extends ControllerSpecSupport {

  lazy val ninoRoute: String = routes.NinoController.submit.url
  lazy val ninoView: NinoView = inject[NinoView]

  val pageTitle = "Provide your National Insurance number"


  def controller() = new NinoController(
    ninoView,
    mockNGRConnector,
    mockAuthJourney,
    mcc
  )

  "Nino Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration()
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
      "Return OK and the correct view with nino" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration(nino = Some(Nino("AA000003D")))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("AA000003D")
      }
    }

    "method submit" must {
      "Successfully submit valid phone number and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "AA000003D")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit with no phone number and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your National Insurance number")
      }

      "Submit incorrect phone number format and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "uk07953009506")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a National Insurance number in the correct format")
      }
    }
  }
}
