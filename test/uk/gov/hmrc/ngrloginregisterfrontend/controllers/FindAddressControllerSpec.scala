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
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER}
import play.api.libs.json.JsResult.Exception
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Call, Session}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{BadRequestException, HeaderNames}
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup.{AddressLookupErrorResponse, AddressLookupSuccessResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupResponseModel, LookedUpAddressWrapper}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView

import scala.concurrent.Future

class FindAddressControllerSpec extends ControllerSpecSupport with TestSupport with TestData {
  lazy val submitRoute: Call = routes.FindAddressController.submit(confirmContactDetailsMode)
  lazy val addressResponseKey: String = "Address-Lookup-Response"
  lazy val postcodeKey: String = "Postcode-Key"
  lazy val view: FindAddressView = inject[FindAddressView]
  val pageTitle = "Find the contact address"
  lazy val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson14.as[Seq[LookedUpAddressWrapper]]
  lazy val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()
  val session: Session = Session(Map(addressResponseKey -> expectAddressesJsonString, postcodeKey -> "AA00 0AA"))

  def controller() = new FindAddressController(
    view,
    mockAddressLookupConnector,
    mockSessionManager,
    mockAuthJourney,
    mockNgrFindAddressRepo,
    mcc
  )(ec, mockConfig)

  "FindAddressController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid postcode and redirect to address search result page with mode as confirm contact details" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("postcode-value", "AA00 0AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("AA00 0AA")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddressSearchResultController.show(page = 1, confirmContactDetailsMode).url)
      }

      "Successfully submit valid postcode and redirect to address search result page with mode as check your answers" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(("postcode-value", "AA00 0AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("AA00 0AA")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddressSearchResultController.show(page = 1, checkYourAnswersMode).url)
      }

      "Successfully submit only valid postcode and redirect to address search result page" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any)).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe SEE_OTHER
      }

      "Successfully submit valid postcode but AddressLookup throws a BadRequestException" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupErrorResponse(new BadRequestException(""))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe BAD_REQUEST
      }

      "Successfully submit valid postcode but AddressLookup throws an error" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupErrorResponse(Exception(JsError("INVALID JSON")))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("postcode-value", "W126WA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "Submit with no postcode and display error message" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(submitRoute)
          .withFormUrlEncodedBody(("postcode-value", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}