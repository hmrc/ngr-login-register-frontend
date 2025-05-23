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
import play.api.Play.materializer
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NameView
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, RatepayerRegistration}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Name
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation

import scala.concurrent.Future

class NameControllerSpec extends ControllerSpecSupport with TestData {

  lazy val nameRoute: Call = routes.NameController.submit(confirmContactDetailsMode)
  lazy val nameView: NameView = inject[NameView]

  val pageTitle = "Contact name"

  def controller() = new NameController(
    nameView,
    mockRatepayerRegistraionRepo,
    mockIsRegisteredCheck,
    mockHasMandotoryDetailsAction,
    mockAuthJourney,
    mcc
  )

  "Phone Number Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockRatepayerRegistraionRepo.findByCredId(any()))
          .thenReturn(Future.successful(None))
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
      "Return OK and the correct view with data" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration(name = Some(Name("Jeffrey")))
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        when(mockRatepayerRegistraionRepo.findByCredId(any()))
          .thenReturn(Future.successful(Some(model)))
        val result = controller().show(confirmContactDetailsMode)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid name and redirect to confirm contact details" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(nameRoute)
          .withFormUrlEncodedBody(("name-value", "Jake"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ConfirmContactDetailsController.show().url)
      }

      "Successfully submit valid name and redirect to check your answers" in {
        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.NameController.submit(checkYourAnswersMode))
          .withFormUrlEncodedBody(("name-value", "Jake"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
      }

      "Submit with no name and display error message" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(nameRoute)
          .withFormUrlEncodedBody(("name-value", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter your contact name")
      }

      "Submit with invalid name and display error message" in {
        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(nameRoute)
          .withFormUrlEncodedBody(("name-value", "!name"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
        content must include("Enter a contact name in the correct format")
      }
    }
  }

}
