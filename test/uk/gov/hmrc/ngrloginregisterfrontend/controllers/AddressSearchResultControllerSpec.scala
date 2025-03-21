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

  lazy val addressSearchResultRoute: String = routes.AddressSearchResultController.show(page = 1).url
  lazy val addressSearchResultView: AddressSearchResultView = inject[AddressSearchResultView]
  lazy val addressResponseKey: String = mockSessionManager.addressLookupResponseKey
  val pageTitle = s"Search results for CH27RH"

  def controller() = new AddressSearchResultController(
    addressSearchResultView,
    mockAuthJourney,
    mcc,
    mockSessionManager
  )

  val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson.as[Seq[LookedUpAddressWrapper]]
  val expectAddressesJsonString: String = Json.toJson(addressLookupResponses.map(_.address)).toString()

  "Address Search Result Controller" must {
    "method show" must {
      "Return OK and the correct view when theirs 14 address on page 1" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString))
        val result = controller().show()(authenticatedFakeRequestWithSession)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must       include("Showing <strong>1</strong> to <strong>5</strong> of <strong>14</strong> items.")
        content must       include("Next")
        content mustNot    include("Previous")
      }

      "Correctly display page number and number for no address" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
        val result = controller().show()(authenticatedFakeRequestWithSession)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must       include("Showing <strong>1</strong> to <strong>0</strong> of <strong>0</strong> items.")
      }

      "Correctly display page number and number of address's on page 2" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString))
        val result = controller().show(page = 2)(authenticatedFakeRequestWithSession)
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString))
        status(result) mustBe OK
        val content = contentAsString(result)
        content must    include("Previous")
        content must    include ("Showing <strong>6</strong> to <strong>10</strong> of <strong>14</strong> items.")
        content must include("Next")
      }

      "Correctly display page number and number of address's on page 3" in {
        val result = controller().show(page = 3)(authenticatedFakeRequestWithSession)
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString))
        status(result) mustBe OK
        val content = contentAsString(result)
        content must    include("Previous")
        content must    include ("Showing <strong>11</strong> to <strong>14</strong> of <strong>14</strong> items.")
        content mustNot include("Next")
      }
    }

    "method selectedAddress" must {
      "Return SEE OTHER and correctly store address to the session" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some(expectAddressesJsonString))
        when(mockSessionManager.setChosenAddress(any(), any())) thenReturn Session(Map("NGR-Chosen-Address-Key" -> "20, Long Rd, Bournemouth, Dorset, BN110AA, UK"))
        val result = controller().selectedAddress(1)(authenticatedFakeRequestWithSession)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmAddressController.show.url)
      }
      "Address index out of bounds exception thrown" in {
        when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
        when(mockSessionManager.setChosenAddress(any(), any())) thenReturn Session(Map("NGR-ChosenAddressIdKey" -> "20, Long Rd, Bournemouth, Dorset, BN110AA, UK"))
        val exception = intercept[RuntimeException] {
          controller().selectedAddress(1)(authenticatedFakeRequestWithSession).futureValue
        }
        exception.getMessage must include("Address not found at index")
      }
    }
  }
}
