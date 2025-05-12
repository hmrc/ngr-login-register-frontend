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
import play.api.http.Status.{CREATED, OK, SEE_OTHER}
import play.api.mvc.{ActionBuilder, ActionFunction, AnyContent, BodyParser, Request, Result}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.retrieve.Name
import uk.gov.hmrc.auth.core.{AffinityGroup, ConfidenceLevel, Nino}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.actions.AuthRetrievals
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.RegistrationCompleteView

import scala.concurrent.{ExecutionContext, Future}

class RegistrationCompleteControllerSpec extends ControllerSpecSupport {

  lazy val RegistrationCompleteRecoveryId: String = routes.RegistrationCompleteController.show(Some("12345xyz")).url
  lazy val testView: RegistrationCompleteView = inject[RegistrationCompleteView]

  val pageTitle1 = "Registration Successful"
  val pageTitle2 = "Register for the business rates valuation service"
  val authenticate = mockAuthRetrievals(hasCredId = true, hasNino = true)

  def mockAuthRetrievals(hasCredId: Boolean = true, hasNino: Boolean = true): AuthRetrievals = {
    new AuthRetrievals {
      override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] = {
        // Create a test request with all the necessary fields
        val typedRequest = AuthenticatedUserRequest(
          request = request,
          confidenceLevel = Some(ConfidenceLevel.L250),
          authProvider = Some("GovernmentGateway"),
          nino = Nino(hasNino = hasNino, if (hasNino) Some("AA000003D") else None),
          email = Some("user@example.com"),
          credId = if (hasCredId) Some("1234") else None,
          affinityGroup = Some(AffinityGroup.Individual),
          name = Some(Name(Some("Test"), Some("User")))
        )
        // Run the controller action with our test request
        block(typedRequest)
      }
      // Required implementations
      override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
      override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
    }
  }

  def controller() = new RegistrationCompleteController(
    testView,
    mockIsRegisteredCheck,
    authenticate,
    mockNGRConnector,
    mcc
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    val ratepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId)
    val response: Option[RatepayerRegistrationValuation] = Some(ratepayer)
    when(mockNGRConnector.getRatepayer(any())(any()))
      .thenReturn(Future.successful(response))
  }

  "RegistrationComplete Controller" must {
    "method show" must {

      "Return OK and the correct view" in {
        mockAuthRetrievals()
          val result = controller.show(Some("recovery-id")).apply(authenticatedFakeRequestWithEmail)
          status(result) mustBe OK
          val content = contentAsString(result)
          content must include(pageTitle1)
      }

      "throw exception when email is not found " in {
        mockAuthRetrievals()
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
          mockAuthJourney,
          mockNGRConnector,
          mcc
        )
        val result = controller().submit(Some("12345xyz"))(authenticatedFakeRequestWithEmail)
        status(result) mustBe SEE_OTHER
      }
    }
  }
}
