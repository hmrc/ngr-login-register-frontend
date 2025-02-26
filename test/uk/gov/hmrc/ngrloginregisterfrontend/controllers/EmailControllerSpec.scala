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
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView

class EmailControllerSpec extends ControllerSpecSupport {

  lazy val emailRoute: String = routes.EmailController.submit.url
  lazy val emailView: EmailView = inject[EmailView]

  val pageTitle = "Email"

  def controller() = new EmailController(
    emailView,
    mockAuthJourney,
    mcc
  )

  "Email Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid email and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EmailController.submit).withFormUrlEncodedBody(("email-value", "test@test.co.uk")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe SEE_OTHER
      }

      "Submit with no email address and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EmailController.submit).withFormUrlEncodedBody(("email-value", "")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your email address")
      }

      "Submit incorrect email address format and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EmailController.submit).withFormUrlEncodedBody(("email-value", "@test.co.uk")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a valid email address")
      }

      "Submit incorrect email address with total garbage and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EmailController.submit).withFormUrlEncodedBody(("email-value", "diuewqhiupdhewtest@test.co.uktest@test.co.uk")).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a valid email address")
      }
    }
  }
}
