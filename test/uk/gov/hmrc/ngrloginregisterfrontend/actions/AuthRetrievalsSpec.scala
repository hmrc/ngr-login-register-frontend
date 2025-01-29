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
import org.mockito.Mockito.{spy, times, verify, when}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.utils.EqualsAuthenticatedUserRequest

import scala.concurrent.{ExecutionContext, Future}
class AuthRetrievalsSpec extends TestSupport{

  override implicit lazy val app: Application = GuiceApplicationBuilder().build()

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]

  private object Stubs {
    def successBlock(request: AuthenticatedUserRequest[_]): Future[Result] = Future.successful(Ok(""))
  }

  private val testRequest = FakeRequest("GET", "/paye/company-car")

  val authAction = new AuthRetrievalsImpl(mockAuthConnector, mcc)

  private implicit class HelperOps[A](a: A) {
    def ~[B](b: B) = new ~(a, b)
  }

  "Auth Action" when {
    "a user navigating to /ngr-login-register-frontend/start" must {
      "the user has a confidence level of 250 with all details" in {

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

        val stubs = spy(Stubs)

        val result = authAction.invokeBlock(testRequest, stubs.successBlock)

        val expectedRequest = AuthenticatedUserRequest(
          request = testRequest,
          credId = Some(testCredId.providerId),
          authProvider = Some(testCredId.providerType),
          nino = Nino(true, Some(testNino)),
          confidenceLevel = Some(testConfidenceLevel),
          email = Some(testEmail),
          affinityGroup = Some(testAffinityGroup),
          name = Some(testName)
        )

        status(result) mustBe OK

        verify(stubs, times(1)).successBlock(argThat(EqualsAuthenticatedUserRequest(expectedRequest)))
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

      //      "the user has a trusted helper" in {
      //
      //        val retrievalResult: Future[authAction.RetrievalsType] =
      //          Future.successful(Some("extId") ~ Some(nino.nino) ~ Some("000111222") ~ Some(trustedHelper))
      //
      //        when(
      //          mockAuthConnector
      //            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
      //        )
      //          .thenReturn(retrievalResult)
      //
      //        val stubs = spy(Stubs)
      //
      //        val result = authAction.invokeBlock(testRequest, stubs.successBlock)
      //
      //        val expectedRequest = AuthenticatedRequest(
      //          request = testRequest,
      //          externalId = "extId",
      //          nino = delegatedNino.nino,
      //          saUtr = None,
      //          trustedHelper = Some(trustedHelper)
      //        )
      //
      //        status(result) mustBe OK
      //        verify(stubs, times(1)).successBlock(argThat(EqualsAuthenticatedRequest(expectedRequest)))
      //      }
      //
      //      "successfully authorise the user" in {
      //        val validBearerToken                                   = "bearer_token"
      //        val retrievalResult: Future[authAction.RetrievalsType] =
      //          Future.successful(Some("extId") ~ Some(nino.nino) ~ Some("000111222") ~ Some(trustedHelper))
      //
      //        when(
      //          mockAuthConnector
      //            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
      //        )
      //          .thenReturn(retrievalResult)
      //
      //        val testRequestWithToken = testRequest.withHeaders("Authorization" -> s"Bearer $validBearerToken")
      //        val result               = authAction.invokeBlock(testRequestWithToken, Stubs.successBlock)
      //
      //        testRequestWithToken.headers.get("Authorization") mustBe Some(s"Bearer $validBearerToken")
      //        status(result) mustBe OK
      //      }
      //    }
      //
      //    "the user doesn't have sufficient enrolments" must {
      //      "be successfully redirected to the service page" in {
      //
      //        val retrievalResult: Future[authAction.RetrievalsType] =
      //          Future.successful(Some("extId") ~ Some(nino.nino) ~ None ~ None)
      //
      //        when(
      //          mockAuthConnector
      //            .authorise[authAction.RetrievalsType](any(), any())(any(), any())
      //        )
      //          .thenReturn(retrievalResult)
      //
      //        val stubs = spy(Stubs)
      //
      //        val result = authAction.invokeBlock(testRequest, stubs.successBlock)
      //
      //        status(result) mustBe OK
      //
      //        val expectedRequest = AuthenticatedRequest(
      //          request = testRequest,
      //          externalId = "extId",
      //          nino = nino.nino,
      //          saUtr = None,
      //          trustedHelper = None
      //        )
      //
      //        verify(stubs, times(1)).successBlock(argThat(EqualsAuthenticatedRequest(expectedRequest)))
      //      }
      //    }
      //
      //    "is not logged in" must {
      //      "throw a MissingBearerToken exception" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new MissingBearerToken))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[MissingBearerToken]
      //        }
      //      }
      //
      //      "throw a InvalidBearerToken exception" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new InvalidBearerToken))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[InvalidBearerToken]
      //        }
      //      }
      //    }
      //
      //    "the user's session has expired" must {
      //      "redirect the user to log in " in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new BearerTokenExpired))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[BearerTokenExpired]
      //        }
      //      }
      //    }
      //
      //    "the user doesn't have sufficient enrolments" must {
      //      "redirect the user to the unauthorised page" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new InsufficientEnrolments))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[InsufficientEnrolments]
      //        }
      //      }
      //    }
      //
      //    "the user doesn't have sufficient confidence level" must {
      //      "redirect the user to the unauthorised page" in {
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new InsufficientConfidenceLevel))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[InsufficientConfidenceLevel]
      //        }
      //      }
      //    }
      //
      //    "the user used an unaccepted auth provider" must {
      //      "redirect the user to the unauthorised page" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new UnsupportedAuthProvider))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[UnsupportedAuthProvider]
      //        }
      //      }
      //    }
      //
      //    "the user has an unsupported affinity group" must {
      //      "redirect the user to the unauthorised page" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new UnsupportedAffinityGroup))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[UnsupportedAffinityGroup]
      //        }
      //      }
      //    }
      //
      //    "the user has an unsupported credential role" must {
      //      "redirect the user to the unauthorised page" in {
      //
      //        when(mockAuthConnector.authorise(any(), any())(any(), any()))
      //          .thenReturn(Future.failed(new UnsupportedCredentialRole))
      //
      //        val result = authAction.invokeBlock(testRequest, Stubs.successBlock)
      //
      //        whenReady(result.failed) { e =>
      //          e mustBe a[UnsupportedCredentialRole]
      //        }
      //      }
      //    }
      //
      //  }
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