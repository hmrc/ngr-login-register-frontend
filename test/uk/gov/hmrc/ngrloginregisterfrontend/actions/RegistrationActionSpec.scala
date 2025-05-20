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

package uk.gov.hmrc.ngrloginregisterfrontend.actions


import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{spy, when}
import play.api.Application
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistraionRepo

import scala.concurrent.Future

class RegistrationActionSpec extends TestSupport with TestData{

  override implicit lazy val app: Application = GuiceApplicationBuilder().build()

  private val mockNGRConnector: NGRConnector = mock[NGRConnector]
  private val mockRatepayerRegistraionRepo: RatepayerRegistraionRepo = mock[RatepayerRegistraionRepo]

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockAuthAction = new AuthRetrievalsImpl(mockAuthConnector, mcc)

  private val mockCitizenDetailsConnector:CitizenDetailsConnector = mock[CitizenDetailsConnector]

  private object Stubs {
    def successBlock(request: Request[AnyContent]): Future[Result] = Future.successful(Ok(""))
  }

  private val testRequest = FakeRequest("GET", "/paye/company-car")

  val registrationAction = new RegistrationActionImpl(
    ngrConnector = mockNGRConnector,
    mongo = mockRatepayerRegistraionRepo,
    citizenDetailsConnector = mockCitizenDetailsConnector,
    authenticate = mockAuthAction,
    appConfig = mockConfig,
    mcc)

  private implicit class HelperOps[A](a: A) {
    def ~[B](b: B) = new ~(a, b)
  }

  val retrievalResult: Future[mockAuthAction.RetrievalsType] =
    Future.successful(
      Some(testCredId) ~
        Some(testNino) ~
        testConfidenceLevel ~
        Some(testEmail) ~
        Some(testAffinityGroup) ~
        Some(testName)
    )

  "Registration Action" when {
    "a user navigating to /ngr-login-register-frontend/start" must {
      when(
        mockAuthConnector
          .authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any())
      ).thenReturn(retrievalResult)
        "must be navigated to requested page if found in the front end mongo to not be registered" in {
          when(
            mockRatepayerRegistraionRepo.findByCredId(any())
          ).thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel)))))

          val stubs = spy(Stubs)

          val authResult = mockAuthAction.invokeBlock(testRequest, stubs.successBlock)
          status(authResult) mustBe OK

          val result = registrationAction.invokeBlock(testRequest, stubs.successBlock)
          status(result) mustBe OK
        }
      "must be navigated to requested page if found in the back end mongo to not be registered" in {
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel)))))

        val stubs = spy(Stubs)

        val authResult = mockAuthAction.invokeBlock(testRequest, stubs.successBlock)
        status(authResult) mustBe OK

        val result = registrationAction.invokeBlock(testRequest, stubs.successBlock)
        status(result) mustBe OK
      }

      "must add the user to the front end mongo if no user is found" in {
        when(mockRatepayerRegistraionRepo.findByCredId(any()))
          .thenReturn(Future.successful(None))
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(None))
        when(mockRatepayerRegistraionRepo.upsertRatepayerRegistration(any()))
          .thenReturn(Future.successful(true))
        when(mockCitizenDetailsConnector.getPersonDetails(any())(any()))
          .thenReturn(Future.successful(Right(personDetailsResponse)))
        when(mockRatepayerRegistraionRepo.upsertRatepayerRegistration(any()))
          .thenReturn(Future.successful(true))

        val stubs = spy(Stubs)

        val authResult = mockAuthAction.invokeBlock(testRequest, stubs.successBlock)
        status(authResult) mustBe OK

        val result = registrationAction.invokeBlock(testRequest, stubs.successBlock)
        status(result) mustBe OK
      }

      "must be navigated to dashboard page if registered" in {
        when(
          mockRatepayerRegistraionRepo.findByCredId(any())
        ).thenReturn(Future.successful(None))

        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(isRegistered = Some(true)))))))


        val stubs = spy(Stubs)


        val authResult = mockAuthAction.invokeBlock(testRequest, stubs.successBlock)
        status(authResult) mustBe OK

        val result = registrationAction.invokeBlock(testRequest, stubs.successBlock)
        status(result) mustBe SEE_OTHER
      }
      }
    }
}
