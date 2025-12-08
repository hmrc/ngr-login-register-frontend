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
import play.api.mvc.{AnyContent, BodyParser, Request, Result}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.AuthRetrievals
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.RegistrationCompleteView

import scala.concurrent.{ExecutionContext, Future}

class RegistrationCompleteControllerSpec extends ControllerSpecSupport with TestData {

  lazy val RegistrationCompleteRecoveryId: String = routes.RegistrationCompleteController.show(Some("12345xyz")).url
  lazy val testView: RegistrationCompleteView = inject[RegistrationCompleteView]

  val pageTitle1 = "Registration Successful"
  val pageTitle2 = "Register for the business rates valuation service"
  //val authenticate = mockAuthRetrievals(hasCredId = true, hasNino = true)


    def mockAuthRetrievals(hasCredId: Boolean = true, hasNino: Boolean = true): AuthRetrievals = {
      new AuthRetrievals {
        override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {
          val fakeReq = RatepayerRegistrationValuationRequest(
            request = request,
            credId = CredId("1234"),
            ratepayerRegistration = Some(testRegistrationModel)
          )
          block(fakeReq)
        }
        // Required implementations
        override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
        override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
      }
    }

  def controller() = new RegistrationCompleteController(
    testView,
    mockIsRegisteredCheck,
    mockHasMandotoryDetailsAction,
    mockAuthRetrievals(),
    mockNGRConnector,
    mcc,
    mockAuditingService
  )

  "RegistrationComplete Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val ratepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId)
        val response: Option[RatepayerRegistrationValuation] = Some(ratepayer)
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(response))
          val result = controller().show(Some("recovery-id")).apply(authenticatedFakeRequestWithEmail)
          status(result) mustBe OK
          val content = contentAsString(result)
          content must include(pageTitle1)
      }
      "throw exception when email is not found " in {
        when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
        val exception = intercept[RuntimeException] {
          controller().show(Some("12345xyz"))(authenticatedFakeRequest).futureValue
        }
        exception.getMessage must include("Can not find ratepayer email in the database")
      }
    }
    "method submit" must {
      "Return SEE_OTHER" in {
        mockRequest()
        def controller() = new RegistrationCompleteController(
          testView,
          mockIsRegisteredCheck,
          mockHasMandotoryDetailsAction,
          mockAuthJourney,
          mockNGRConnector,
          mcc,
          mockAuditingService
        )
        val result = controller().submit(Some("12345xyz"))(authenticatedFakeRequestWithEmail)
        status(result) mustBe SEE_OTHER
      }
    }
  }
}
