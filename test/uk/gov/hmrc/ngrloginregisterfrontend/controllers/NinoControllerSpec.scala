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
import uk.gov.hmrc.auth.core.{Nino => authNino}
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.{NINO, SAUTR}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{RatepayerRegistrationValuation, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoView

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

  override def beforeEach(): Unit = {
    mockRequest()
  }

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
        val ratepayer: RatepayerRegistration = RatepayerRegistration(trnReferenceNumber = Some(TRNReferenceNumber(NINO, "AA000003D")))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("AA000003D")
      }

      "Return OK and the correct view if nino is missing from TRNReferenceNumber" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration(trnReferenceNumber = Some(TRNReferenceNumber(SAUTR, "1097133333")))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
        content mustNot include("AA000003D")
      }

      "Return OK and the correct view if ratepayer is not found" in {
        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "throw exception when nino is not found from auth" in {
        mockRequest(hasNino = false)
        val exception = intercept[RuntimeException] {
          controller().show()(authenticatedFakeRequest).futureValue
        }
        exception.getMessage mustBe "No nino found from auth"
      }
    }

    "method submit" must {
      "Successfully submit valid matching nino and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "AA000003D")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Successfully submit valid matching nino that contains spaces and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "AA 00 00 03 D")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit invalid nino display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit)
          .withFormUrlEncodedBody(("nino-value", "AA000003E"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a National Insurance number in the correct format")
      }

      "Submit valid non matching nino display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit)
          .withFormUrlEncodedBody(("nino-value", "AA000009D"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a valid National Insurance number")
      }

      "Submit with no nino and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your National Insurance number")
      }

      "Submit incorrect nino format and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NinoController.submit).withFormUrlEncodedBody(("nino-value", "uk07953009506")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = authNino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a National Insurance number in the correct format")
      }

      "throw exception when nino is not found from auth" in {
        mockRequest(hasNino = false)
        val exception = intercept[RuntimeException] {
          controller().submit()(authenticatedFakeRequest).futureValue
        }
        exception.getMessage mustBe "No nino found from auth"
      }
    }
  }
}
