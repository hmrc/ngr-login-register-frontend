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
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupResponseModel, LookedUpAddressWrapper}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Postcode, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{FindAddressView, ManualAddressView}

import scala.concurrent.Future

class ManualAddressControllerSpec extends ControllerSpecSupport with TestSupport with TestData {

  lazy val manualAddressRoute: Call = routes.ManualAddressController.submit(confirmContactDetailsMode)
  lazy val manualAddressView: ManualAddressView = inject[ManualAddressView]
  lazy val view: FindAddressView = inject[FindAddressView]
  val pageTitle = "What is the address?"

  lazy val addressResponseKey: String = "Address-Lookup-Response"
  lazy val postcodeKey: String = "Postcode-Key"
  lazy val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson14.as[Seq[LookedUpAddressWrapper]]
  lazy val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()
  val session: Session = Session(Map(addressResponseKey -> expectAddressesJsonString, postcodeKey -> "AA00 0AA"))

  def controller() = new ManualAddressController(
    manualAddressView,
    mockNGRConnector,
    mockAddressLookupConnector,
    mockSessionManager,
    mockAuthJourney,
    mcc
  )( mockConfig, ec)

  "Manual Address Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration()
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))   .thenReturn(Future.successful(Some(model)))
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
      "Return OK and the correct view with address" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration(address = Some(Address(line1 = "99", line2 = Some("Wibble Rd"), town = "Worthing", county = Some("West Sussex"), postcode = Postcode("AA00 0AA"))))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid postcode and redirect to address search result page with mode as confirm contact details" in {
        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(manualAddressRoute)
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode" -> "AA00 0AA"
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("AA00 0AA")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddressSearchResultController.show(page = 1, confirmContactDetailsMode).url)
      }

      "Successfully submit valid postcode and redirect to address search result page with mode as check your answers" in {
        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.ManualAddressController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode" -> "AA00 0AA"
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("AA00 0AA")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddressSearchResultController.show(page = 1, checkYourAnswersMode).url)
      }

      "Successfully submit only valid postcode was entered and redirect to address search result page with mode as check your answers" in {
        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))))
        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.ManualAddressController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(
            "AddressLine1" -> "",
            "AddressLine2" -> "",
            "City" -> "",
            "PostalCode" -> "AA00 0AA"
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("AA00 0AA")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddressSearchResultController.show(page = 1, checkYourAnswersMode).url)
      }

      "Successfully submit valid postcode but AddressLookup throws a BadRequestException" in {
        when(mockSessionManager.setAddressLookupResponse(any(), any())).thenReturn(session)
        when(mockSessionManager.setPostcode(any(), any())).thenReturn(session)
        when(mockAddressLookupConnector.findAddressByPostcode(any(), any())(any(), any())).thenReturn(Future.successful(AddressLookupErrorResponse(new BadRequestException(""))))
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(manualAddressRoute)
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode" -> "W126WA"
          )
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
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(manualAddressRoute)
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode" -> "W126WA"
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        result.map(result => {
          result.session.get(addressResponseKey) mustBe Some(expectAddressesJsonString)
          result.session.get(postcodeKey) mustBe Some("BN110AA")
        })
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "Submit with no postcode and display error message" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(manualAddressRoute)
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode"-> ""
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter postcode")
      }

      "Submit with invalid postcode and display error message" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(manualAddressRoute)
          .withFormUrlEncodedBody(
            "AddressLine1" -> "99",
            "AddressLine2" -> "Wibble Rd",
            "City" -> "Worthing",
            "PostalCode"-> "W12A6WA"
          )
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a full UK postcode")
      }
    }
  }
  "method setFilter" must {
    "Successfully return correct string" in {
      val address: Address = Address("99", Some("Wibble Rd"), "Worthing", None, Postcode("W126WA"))
      val result = controller().setFilter(address)
      result mustBe Some("99 Wibble Rd Worthing")
    }

    "Return None when address is empty" in {
      val address: Address = Address("", None, "", None, Postcode("W126WA"))
      val result = controller().setFilter(address)
      result mustBe None
    }
  }
}
