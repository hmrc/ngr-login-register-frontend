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
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ContactNumber, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.PhoneNumberView

import scala.concurrent.Future

class PhoneNumberControllerSpec extends ControllerSpecSupport {

  lazy val phoneNumberRoute: String = routes.PhoneNumberController.submit.url
  lazy val phoneNumberView: PhoneNumberView = inject[PhoneNumberView]

  val pageTitle = "Phone Number"


  def controller() = new PhoneNumberController(
    phoneNumberView,
    mockNGRConnector,
    mockAuthJourney,
    mcc
  )

  "Phone Number Controller" must {
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
      "Return OK and the correct view with phone number" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration(contactNumber = Some(ContactNumber("07878787878")))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid phone number and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.PhoneNumberController.submit).withFormUrlEncodedBody(("phoneNumber-value", "07953009506")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit with no phone number and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.PhoneNumberController.submit).withFormUrlEncodedBody(("phoneNumber-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your Phone number")
      }

      "Submit incorrect phone number format and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.PhoneNumberController.submit).withFormUrlEncodedBody(("phoneNumber-value", "uk07953009506")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Please enter a valid phone number")
      }

      "Submit incorrect phone number with more than 24 digits and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.PhoneNumberController.submit).withFormUrlEncodedBody(("phoneNumber-value", "0795300950607953009506506")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Please enter a valid phone number")
      }
    }
  }
}
