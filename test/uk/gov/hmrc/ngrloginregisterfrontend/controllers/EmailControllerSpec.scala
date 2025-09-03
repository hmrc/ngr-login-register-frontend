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

///*
// * Copyright 2025 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.ngrloginregisterfrontend.controllers
//
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.when
//import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
//import play.api.Play.materializer
//import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
//import play.api.mvc.Call
//import play.api.test.FakeRequest
//import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
//import uk.gov.hmrc.auth.core.Nino
//import uk.gov.hmrc.http.HeaderNames
//import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
//import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email
//import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
//import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, RatepayerRegistration}
//import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView
//
//import scala.concurrent.Future
//
//class EmailControllerSpec extends ControllerSpecSupport with TestData {
//
//  lazy val emailRoute: Call = routes.EmailController.submit(confirmContactDetailsMode)
//  lazy val emailView: EmailView = inject[EmailView]
//
//  val pageTitle = "Enter email address"
//
//  def controller() = new EmailController(
//    emailView,
//    mockRatepayerRegistraionRepo,
//    mockIsRegisteredCheck,
//    mockHasMandotoryDetailsAction,
//    mockAuthJourney,
//    mcc
//  )
//
//  "Email Controller" must {
//    "method show" must {
//      "Return OK and the correct view" in {
//        val result = controller().show(confirmContactDetailsMode)(ratepayerRegistrationValuationRequest)
//        status(result) mustBe OK
//        val content = contentAsString(result)
//        content must include(pageTitle)
//      }
//
//      "Return OK and the correct view with no data" in {
//        val ratepayer: RatepayerRegistration = RatepayerRegistration(email = None)
//        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
//        when(mockNGRConnector.getRatepayer(any())(any()))
//          .thenReturn(Future.successful(Some(model)))
//        val result = controller().show(confirmContactDetailsMode)(ratepayerRegistrationValuationRequest)
//        status(result) mustBe OK
//        val content = contentAsString(result)
//        content must include(pageTitle)
//      }
//
//      "Return OK and the correct view with data" in {
//        val ratepayer: RatepayerRegistration = RatepayerRegistration(email = Some(Email("yes@no.biz")))
//        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
//        when(mockNGRConnector.getRatepayer(any())(any()))
//          .thenReturn(Future.successful(Some(model)))
//        val result = controller().show(confirmContactDetailsMode)(ratepayerRegistrationValuationRequest)
//        status(result) mustBe OK
//        val content = contentAsString(result)
//        content must include(pageTitle)
//      }
//    }
//
//    "method submit" must {
//      "Successfully submit valid email and redirect to confirm contact details" in {
//        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(emailRoute)
//          .withFormUrlEncodedBody(("email-value", "test@test.co.uk"))
//          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
//        status(result) mustBe SEE_OTHER
//        redirectLocation(result) shouldBe Some(routes.ConfirmContactDetailsController.show().url)
//      }
//
//      "Successfully submit valid email and redirect to check your answers" in {
//        val result = controller().submit(checkYourAnswersMode)(AuthenticatedUserRequest(FakeRequest(routes.EmailController.submit(checkYourAnswersMode))
//          .withFormUrlEncodedBody(("email-value", "test@test.co.uk"))
//          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
//        status(result) mustBe SEE_OTHER
//        redirectLocation(result) shouldBe Some(routes.CheckYourAnswersController.show.url)
//      }
//
//      "Submit with no email address and display error message" in {
//        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(emailRoute)
//          .withFormUrlEncodedBody(("email-value", ""))
//          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
//        status(result) mustBe BAD_REQUEST
//        val content = contentAsString(result)
//        content must include(pageTitle)
//        content must include("Enter your email address")
//      }
//
//      "Submit incorrect email address format and display error message" in {
//        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(emailRoute)
//          .withFormUrlEncodedBody(("email-value", "@test.co.uk"))
//          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
//        status(result) mustBe BAD_REQUEST
//        val content = contentAsString(result)
//        content must include(pageTitle)
//        content must include("Enter a valid email address")
//      }
//
//      "Submit incorrect email address with total garbage and display error message" in {
//        val result = controller().submit(confirmContactDetailsMode)(AuthenticatedUserRequest(FakeRequest(emailRoute)
//          .withFormUrlEncodedBody(("email-value", "diuewqhiupdhewtest@test.co.uktest@test.co.uk"))
//          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
//        status(result) mustBe BAD_REQUEST
//        val content = contentAsString(result)
//        content must include(pageTitle)
//        content must include("Enter a valid email address")
//      }
//    }
//  }
//}
