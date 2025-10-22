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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{spy, when}
import play.api.Application
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuationRequest

import scala.concurrent.{ExecutionContext, Future}
class AuthRetrievalsSpec extends TestSupport {

  override implicit lazy val app: Application = GuiceApplicationBuilder().build()

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  private val mockAppConfig: AppConfig = mock[AppConfig]

  private object Stubs {
    def successBlock(request: RatepayerRegistrationValuationRequest[_]): Future[Result] = Future.successful(Ok(""))
  }

  private val testRequest = FakeRequest("GET", "/paye/company-car")

  val authAction = new AuthRetrievalsImpl(mockAuthConnector, mockAppConfig, mcc)

  private implicit class HelperOps[A](a: A) {
    def ~[B](b: B) = new ~(a, b)
  }

  "Auth Action" when {
    "a user navigating to /ngr-login-register-frontend/start" must {
      "have a confidence level of 250 with all details and must have a allowed emailId" in {

        val retrievalResult: Future[authAction.RetrievalsType] =
          Future.successful(
            Some(testCredId) ~
              Some(testNino) ~
              testConfidenceLevel ~
              Some(testEmail) ~
              Some(testAffinityGroup) ~
              Some(testName)
          )

        when(
          mockAuthConnector
            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
        )
          .thenReturn(retrievalResult)

        when(mockAppConfig.allowedUserEmailIds).thenReturn(Seq(testEmail))
        when(mockAppConfig.publicAccessAllowed).thenReturn(false)

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        status(result) mustBe OK
      }

      "have a confidence level of 250 with all details and must public-access-allowed flag set to true" in {

        val retrievalResult: Future[authAction.RetrievalsType] =
          Future.successful(
            Some(testCredId) ~
              Some(testNino) ~
              testConfidenceLevel ~
              Some(testEmail) ~
              Some(testAffinityGroup) ~
              Some(testName)
          )

        when(
          mockAuthConnector
            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
        )
          .thenReturn(retrievalResult)

        when(mockAppConfig.allowedUserEmailIds).thenReturn(Seq(testEmail))
        when(mockAppConfig.publicAccessAllowed).thenReturn(false)

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        status(result) mustBe OK
      }

      "redirect to an exit page when does not have a emailId which is in allowedId emailId list" in {

        val retrievalResult: Future[authAction.RetrievalsType] =
          Future.successful(
            Some(testCredId) ~
              Some(testNino) ~
              testConfidenceLevel ~
              Some(testEmail) ~
              Some(testAffinityGroup) ~
              Some(testName)
          )

        when(
          mockAuthConnector
            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
        )
          .thenReturn(retrievalResult)

        when(mockAppConfig.allowedUserEmailIds).thenReturn(Seq("testEmail@test.com"))
        when(mockAppConfig.publicAccessAllowed).thenReturn(false)

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("/ngr-login-register-frontend/register")
      }

      "redirect to an exit page when the feature flag public-access-allowed is set to false" in {

        val retrievalResult: Future[authAction.RetrievalsType] =
          Future.successful(
            Some(testCredId) ~
              Some(testNino) ~
              testConfidenceLevel ~
              Some(testEmail) ~
              Some(testAffinityGroup) ~
              Some(testName)
          )

        when(
          mockAuthConnector
            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
        )
          .thenReturn(retrievalResult)

        when(mockAppConfig.allowedUserEmailIds).thenReturn(Seq.empty)
        when(mockAppConfig.publicAccessAllowed).thenReturn(false)

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("/ngr-login-register-frontend/register")
      }

      "the user has a confidence level of 50 with all details" in {

        val retrievalResult: Future[authAction.RetrievalsType] =
          Future.successful(
            Some(testCredId) ~
              None ~
              ConfidenceLevel.L50 ~
              Some(testEmail) ~
              Some(testAffinityGroup) ~
              Some(testName)
          )

        when(
          mockAuthConnector
            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
        )
          .thenReturn(retrievalResult)

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        whenReady(result.failed){ e =>
          e.getMessage mustBe "confidenceLevel not met"
        }
      }
    }
  }
}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
                                                                           hc: HeaderCarrier,
                                                                           ec: ExecutionContext
  ): Future[A] =
    Future.failed(exceptionToReturn)
}