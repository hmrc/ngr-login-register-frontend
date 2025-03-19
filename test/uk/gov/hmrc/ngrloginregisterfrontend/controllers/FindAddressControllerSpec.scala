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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.mvc.Session
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.AddressLookupResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ErrorResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView

import scala.concurrent.Future

class FindAddressControllerSpec extends ControllerSpecSupport with TestSupport with TestData {
  lazy val submitUrl: String = routes.FindAddressController.submit.url
  lazy val addressResponseKey: String = "Address-Lookup-Response"
  lazy val postcodeKey: String = "Postcode-Key"
  lazy val view: FindAddressView = inject[FindAddressView]
  val pageTitle = "Find the contact address"
  lazy val addressLookupResponses: Seq[AddressLookupResponse] = addressLookupResponsesJson.as[Seq[AddressLookupResponse]]
  lazy val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()
  val session: Session = Session(Map(addressResponseKey -> expectAddressesJsonString, postcodeKey -> "BN110AA"))

  def controller() = new FindAddressController(
    view,
    mockAddressLookupConnector,
    mockSessionManager,
    mockNGRLogger,
    mockAuthJourney,
    mcc
  )(ec, mockConfig)

  "FindAddressController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid postcode and property name and redirect to confirm contact details" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any())(any())).thenReturn(Future.successful(Right(addressLookupResponses)))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"), ("property-name-value", "7"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe SEE_OTHER
      }

      "Successfully submit only valid postcode and redirect to confirm contact details" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any())(any())).thenReturn(Future.successful(Right(addressLookupResponses)))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe SEE_OTHER
      }

      "Successfully submit valid postcode but AddressLookup throws an error" in {
        when(mockAddressLookupConnector.findAddressByPostcode(any())(any())).thenReturn(Future.successful(Left(ErrorResponse(500, "INTERNAL_SERVER_ERROR"))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"), ("property-name-value", "7"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))

        status(result) mustBe INTERNAL_SERVER_ERROR
        val errorResponse: ErrorResponse = contentAsJson(result).as[ErrorResponse]
        errorResponse.message mustBe "INTERNAL_SERVER_ERROR"
      }

      "Submit with no postcode and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit)
          .withFormUrlEncodedBody(("postcode-value", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}