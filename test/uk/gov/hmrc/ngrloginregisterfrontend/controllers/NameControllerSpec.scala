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

import play.api.Play.materializer
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NameView
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames

class NameControllerSpec extends ControllerSpecSupport {

  lazy val nameRoute: String = routes.NameController.submit.url
  lazy val nameView: NameView = inject[NameView]

  val pageTitle = "Contact name"

  def controller() = new NameController(
    nameView,
    mockNGRConnector,
    mockAuthJourney,
    mcc
  )

  "Phone Number Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid name and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NameController.submit).withFormUrlEncodedBody(("name-value", "Jake")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit with no name and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NameController.submit).withFormUrlEncodedBody(("name-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your Contact name")
      }

      "Submit with invalid name and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.NameController.submit).withFormUrlEncodedBody(("name-value", "!name")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a contact name in the correct format")
      }
    }
  }

}
