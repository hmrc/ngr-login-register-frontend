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
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{HeaderNames, HttpResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{LookedUpAddress, LookedUpAddressWrapper}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView

import scala.concurrent.Future

class ConfirmAddressControllerSpec extends ControllerSpecSupport with TestSupport with TestData {
  lazy val submitRoute: Call = routes.ConfirmAddressController.submit(confirmContactDetailsMode, 1)
  lazy val view: ConfirmAddressView = inject[ConfirmAddressView]
  val pageTitle = "Do you want to use this address?"
  lazy val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson14.as[Seq[LookedUpAddressWrapper]]
  lazy val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()

  def controller() = new ConfirmAddressController(
    view,
    mockAuthJourney,
    mockNgrFindAddressRepo,
    mockNGRConnector,
    mcc
  )(mockConfig, ec)

  "ConfirmAddressController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        val result = controller().show(confirmContactDetailsMode, 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Direct to find address when the chosen address doesn't exist in the mongoDB and mode is check your answers" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(None))
        val result = controller().show(checkYourAnswersMode, 1)(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.FindAddressController.show(checkYourAnswersMode).url)
      }
    }

    "method submit" must {
      "Successfully submit when selected no and redirect to confirm contact details" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        val result = controller().submit(confirmContactDetailsMode, 1)(AuthenticatedUserRequest(FakeRequest(submitRoute)
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
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        val result = controller().submit(checkYourAnswersMode, 1)(AuthenticatedUserRequest(FakeRequest(routes.ConfirmAddressController.submit(checkYourAnswersMode, 1))
          .withFormUrlEncodedBody(("confirm-address-radio", "No"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(0)).changeAddress(any(), any())(any())
        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
      }

      "Direct to find address when the chosen address doesn't exist and mode is check your answers" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(None))
        val result = controller().submit(checkYourAnswersMode, 1)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/find-address")
        })
        status(result) mustBe SEE_OTHER
        verify(mockNGRConnector, times(0)).changeAddress(any(), any())(any())
      }

      "Submit with radio buttons unselected and display error message" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        val result = controller().submit(confirmContactDetailsMode, 1)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Direct to find address when submit with radio buttons unselected also the chosen address doesn't exist in the session" in {
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(None))
        val result = controller().submit(confirmContactDetailsMode, 1)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("confirm-address-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.FindAddressController.show(confirmContactDetailsMode).url)
      }

      "Successfully submit when selected yes and redirect to confirm contact details" in {
        val httpResponse = HttpResponse(OK, "Updated Successfully")
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        when(mockNGRConnector.changeAddress(any(), any())(any()))
          .thenReturn(Future.successful(httpResponse))
        val result = controller().submit(confirmContactDetailsMode, 1)(AuthenticatedUserRequest(FakeRequest(submitRoute)
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
        when(mockNgrFindAddressRepo.findChosenAddressByCredId(any(), any())).thenReturn(Future(Some(addressLookupAddress)))
        when(mockNGRConnector.changeAddress(any(), any())(any()))
          .thenReturn(Future.successful(httpResponse))
        val result = controller().submit(checkYourAnswersMode, 1)(AuthenticatedUserRequest(FakeRequest(routes.ConfirmAddressController.submit(checkYourAnswersMode, 1))
          .withFormUrlEncodedBody(("confirm-address-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
      }
    }

    "convertLookedUpAddressToNGRAddress" must {
      "set a chosen address correctly when addressLookup gives 1 line in lines" in {
        val addressLookup: LookedUpAddress = LookedUpAddress(lines = Seq("Line1"), town = "town", postcode = "SW12 6RE", county = None)
        val actual: Address = controller().convertLookedUpAddressToNGRAddress(addressLookup)
        actual shouldBe Address("Line1", None, "town", None, Postcode("SW12 6RE"))
      }

      "set a chosen address correctly when addressLookup gives 2 lines in lines" in {
        val addressLookup: LookedUpAddress = LookedUpAddress(lines = Seq("Line1", "Line2"), town = "town", postcode = "SW12 6RE", county = None)
        val actual: Address = controller().convertLookedUpAddressToNGRAddress(addressLookup)
        actual shouldBe Address("Line1", Some("line2"), "town", None, Postcode("SW12 6RE"))
      }

      "set a chosen address correctly when addressLookup gives 5 lines in  lines" in {
        val addressLookup: LookedUpAddress = LookedUpAddress(lines = Seq("Line1", "Line2", "Line3", "Line4", "Line5"),town = "town", postcode =  "SW12 6RE", county = None)
        val actual: Address = controller().convertLookedUpAddressToNGRAddress(addressLookup)
        actual shouldBe Address("Line1 Line2 Line3", Some("Line4 Line5"), "town", None, Postcode("SW12 6RE"))
      }

      "set a chosen address correctly when addressLookup gives 0 lines in  lines" in {
        val addressLookup: LookedUpAddress = LookedUpAddress(lines = Seq.empty,town = "town", postcode =  "SW12 6RE", county = None)
        val actual: Address = controller().convertLookedUpAddressToNGRAddress(addressLookup)
        actual shouldBe Address("", None, "town", None, Postcode("SW12 6RE"))
      }
    }
  }
}
