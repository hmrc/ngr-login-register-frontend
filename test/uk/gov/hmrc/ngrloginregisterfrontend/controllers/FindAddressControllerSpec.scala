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

import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView

class FindAddressControllerSpec extends ControllerSpecSupport {
  lazy val submitUrl: String = routes.FindAddressController.submit.url
  lazy val view: FindAddressView = inject[FindAddressView]
  val pageTitle = "Find the contact address"

  def controller() = new FindAddressController(
    view,
    mockAuthJourney,
    mcc
  )

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
      "Successfully submit valid postcode and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit).withFormUrlEncodedBody(("postcode-value", "W126WA")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit with no postcode and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAddressController.submit).withFormUrlEncodedBody(("postcode-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
