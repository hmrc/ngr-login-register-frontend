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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.mvc.{Call, Session}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{HeaderNames, HttpResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddressWrapper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView

import scala.concurrent.Future

class ConfirmAddressControllerSpec extends ControllerSpecSupport with TestSupport with TestData {
  lazy val submitRoute: Call = routes.ConfirmAddressController.submit(confirmContactDetailsMode)
  lazy val chosenAddressIdKey: String = "NGR-Chosen-Address-Key"
  lazy val view: ConfirmAddressView = inject[ConfirmAddressView]
  val pageTitle = "Do you want to use this address?"
  lazy val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson14.as[Seq[LookedUpAddressWrapper]]
  lazy val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()
  val session: Session = Session(Map(chosenAddressIdKey -> addressJsonResponse.toString()))

  def controller() = new ConfirmAddressController(
    view,
    mockAuthJourney,
    mockSessionManager,
    mockNGRConnector,
    mcc
  )(mockConfig, ec)

  "ConfirmAddressController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(addressJsonResponse.toString()))
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit when selected no and redirect to confirm contact details" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", "No"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(0)).changeAddress(any(), any())(any())
        redirectLocation(result) shouldBe Some(routes.ConfirmContactDetailsController.show.url)
      }

      "Successfully submit when selected no and redirect to check your answers" in {

        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.ConfirmAddressController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(("confirm-address-radio", "No"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(0)).changeAddress(any(), any())(any())
        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
      }

      "Direct to confirm your contact details when the chosen address doesn't exist" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(0)).changeAddress(any(), any())(any())
      }

      "Submit with radio buttons unselected and display error message" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(addressJsonResponse.toString()))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Successfully submit when selected yes and redirect to confirm contact details" in {
        val httpResponse = HttpResponse(OK, "Updated Successfully")
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(addressJsonResponse.toString()))
        when(mockNGRConnector.changeAddress(any(), any())(any()))
          .thenReturn(Future.successful(httpResponse))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(1)).changeAddress(any(), any())(any())
        redirectLocation(result) shouldBe Some(routes.ConfirmContactDetailsController.show.url)
      }

      "Successfully submit when selected yes and redirect to check your answers" in {
        val httpResponse = HttpResponse(OK, "Updated Successfully")
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(addressJsonResponse.toString()))
        when(mockNGRConnector.changeAddress(any(), any())(any()))
          .thenReturn(Future.successful(httpResponse))
        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.ConfirmAddressController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(("confirm-address-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
      }
    }
  }
}
