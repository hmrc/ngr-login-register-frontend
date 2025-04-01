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
import play.api.libs.json.Json
import play.api.mvc.Session
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddressWrapper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView

class AddressSearchResultControllerSpec extends ControllerSpecSupport with TestData {

  lazy val addressSearchResultRoute: String = routes.AddressSearchResultController.show(page = 1, confirmContactDetailsMode).url
  lazy val addressSearchResultView: AddressSearchResultView = inject[AddressSearchResultView]
  lazy val addressResponseKey: String = mockSessionManager.addressLookupResponseKey
  val pageTitle = s"Search results for CH27RH"

  def controller() = new AddressSearchResultController(
    addressSearchResultView,
    mockAuthJourney,
    mcc,
    mockSessionManager
  )

  val addressLookupResponses14: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson14.as[Seq[LookedUpAddressWrapper]]
  val expectAddressesJsonString14: String = Json.toJson(addressLookupResponses14.map(_.address)).toString()

  val addressLookupResponses32: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson32.as[Seq[LookedUpAddressWrapper]]
  val expectAddressesJsonString32: String = Json.toJson(addressLookupResponses32.map(_.address)).toString()

  "Address Search Result Controller" must {
    "method show" must {
      "Return OK and the correct view when theirs 14 address on page 1" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString14))
        val result = controller().show(page = 1, confirmContactDetailsMode)(authenticatedFakeRequestWithSession)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must       include("Showing <strong>1</strong> to <strong>14</strong> of <strong>14</strong> items.")
        content mustNot       include("Next")
        content mustNot    include("Previous")
      }

      "Correctly display page number and number for no address" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
        val result = controller().show(page = 1, confirmContactDetailsMode)(authenticatedFakeRequestWithSession)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must       include("Showing <strong>0</strong> to <strong>0</strong> of <strong>0</strong> items.")
      }

      "Correctly display page number and number of address's on page 2" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString32))
        val result = controller().show(page = 2, confirmContactDetailsMode)(authenticatedFakeRequestWithSession)
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString32))
        status(result) mustBe OK
        val content = contentAsString(result)
        content must    include("Previous")
        content must    include ("Showing <strong>16</strong> to <strong>30</strong> of <strong>32</strong> items.")
        content must    include("Next")
      }

      "Correctly display page number and number of address's on page 3" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString32))
        val result = controller().show(page = 3, confirmContactDetailsMode)(authenticatedFakeRequestWithSession)
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString32))
        status(result) mustBe OK
        val content = contentAsString(result)
        content must    include("Previous")
        content must    include ("Showing <strong>31</strong> to <strong>32</strong> of <strong>32</strong> items.")
        content mustNot include("Next")
      }
    }

    "method selectedAddress" must {
      "Return SEE OTHER and correctly store address to the session with mode as confirm contact details" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString14))
        when(mockSessionManager.setChosenAddress(any(), any())) thenReturn Session(Map("NGR-Chosen-Address-Key" -> "20, Long Rd, Bournemouth, Dorset, BN110AA"))
        val result = controller().selectedAddress(1, confirmContactDetailsMode)(authenticatedFakeRequestWithSession)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmAddressController.show(confirmContactDetailsMode).url)
      }

      "Return SEE OTHER and correctly store address to the session with mode as check your answers" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString14))
        when(mockSessionManager.setChosenAddress(any(), any())) thenReturn Session(Map("NGR-Chosen-Address-Key" -> "20, Long Rd, Bournemouth, Dorset, BN110AA"))
        val result = controller().selectedAddress(1, checkYourAnswersMode)(authenticatedFakeRequestWithSession)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmAddressController.show(checkYourAnswersMode).url)
      }

      "Address index out of bounds exception thrown" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
        when(mockSessionManager.setChosenAddress(any(), any())) thenReturn Session(Map("NGR-ChosenAddressIdKey" -> "20, Long Rd, Bournemouth, Dorset, BN110AA"))
        val exception = intercept[RuntimeException] {
          controller().selectedAddress(1, confirmContactDetailsMode)(authenticatedFakeRequestWithSession).futureValue
        }
        exception.getMessage must include("Address not found at index")
      }
    }
  }
}
