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

import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, AuthenticatedUserRequest, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView
import play.api.http.Status.{OK}

class AddressSearchResultControllerSpec extends ControllerSpecSupport {

  lazy val addressSearchResultRoute: String = routes.AddressSearchResultController.show(page = 1).url
  lazy val addressSearchResultView: AddressSearchResultView = inject[AddressSearchResultView]


  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel2: Address =
    Address(line1 = "100",
      line2 = Some("Croft Rd"),
      town = "Uxbridge",
      county = Some("Hillingdon"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel3: Address =
    Address(line1 = "20",
      line2 = Some("Long Rd"),
      town = "Bournemouth",
      county = Some("Dorset"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  lazy val testAddressList = Seq(testAddressModel3, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel)
  lazy val testPostcode = testAddressModel.postcode.value

  val pageTitle = s"Search results for ${testPostcode}"

  def controller() = new AddressSearchResultController(
    addressSearchResultView,
    mockAuthJourney,
    mcc
  )

  "Address Search Result Controller" must {
    "method show" must {
      "Return OK and the correct view when theirs 10 address" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
