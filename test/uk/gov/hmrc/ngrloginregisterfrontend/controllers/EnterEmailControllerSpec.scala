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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest

class EnterEmailControllerSpec extends ControllerSpecSupport with TestData {

  lazy val view: EmailView = inject[EmailView]
  def controller() = new EnterEmailController(view, mcc, mockAuthJourney)

  "Email Controller" must {

    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Enter email address")
      }
    }

    "method submit" must {

      "Successfully submit valid email and redirect to confirm contact details" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EnterEmailController.submit)
          .withFormUrlEncodedBody(("email-value", "test@test.co.uk"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ConfirmContactDetailsController.show(Some("test@test.co.uk")).url)
      }

      "Submit with no email address and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EnterEmailController.submit)
          .withFormUrlEncodedBody(("email-value", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("")
        content must include("Enter your email address")
      }

      "Submit incorrect email address format and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EnterEmailController.submit)
          .withFormUrlEncodedBody(("email-value", "@test.co.uk"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("")
        content must include("Enter a valid email address")
      }

      "Submit incorrect email address with total garbage and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.EnterEmailController.submit)
          .withFormUrlEncodedBody(("email-value", "diuewqhiupdhewtest@test.co.uktest@test.co.uk"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("")
        content must include("Enter a valid email address")
      }

    }

  }


}
