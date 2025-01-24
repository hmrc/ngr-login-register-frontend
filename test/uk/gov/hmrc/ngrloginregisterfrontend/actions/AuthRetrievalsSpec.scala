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
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc._
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthProvider, ConfidenceLevel, CredentialRole, Enrolment, Enrolments, Nino}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
class AuthRetrievalsSpec extends TestSupport with GuiceOneAppPerSuite with Injecting with BeforeAndAfterEach {

  implicit val hc = HeaderCarrier(authorization = Some(Authorization("Bearer 123")))
  val mockAuthConnector: AuthConnector = mock[AuthConnector]

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  def appConfig: AppConfig = fakeApplication().injector.instanceOf[AppConfig]

  def httpClient: HttpClientV2 = fakeApplication().injector.instanceOf[HttpClientV2]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
  }
  private val testNino:String = "AA000003D"
  private val testEmail:String = "user@test.com"
  private val testCredId:String = "0000000022"

  private class TestSetupAuthorisationDemoController(stubbedRetrievalResult: Future[Any])

  type RetrievalType = Option[String] ~ Enrolments ~ Option[AffinityGroup] ~ Option[String] ~ Option[Credentials] ~ ConfidenceLevel ~ Option[String]


  def getEnrolmentPredicate(): Predicate =
     Enrolment("IR-PAYE").withIdentifier("NINO", testNino)
      .withDelegatedAuthRule("ers-auth") and ConfidenceLevel.L250

  "calling retrieveProviderIdAndAuthorisedEnrolments" should {
    "return 200 for successful retrieval of 'authProviderId' and 'authorisedEnrolments' from auth-client" in {

      val retrievalResult: Future[~[Credentials, Enrolments]~ConfidenceLevel] = Future.successful(
        new ~(
          new ~(Credentials("gg", "cred-1234"),
        Enrolments(Set(Enrolment("enrolment-value")))),
            ConfidenceLevel.L250)
      )

      Mockito.when(
        mockAuthConnector.authorise[authAction.RetrievalsType](any(), any())(any(), any())
      ).thenReturn(retrievalResult)

      Mockito.when(mockAuthConnector.authorise[~[Credentials, Enrolments]~ ConfidenceLevel](any(), any())(any(), any()))
        .thenReturn(retrievalResult)

      mockAuthConnector.authorise(EmptyPredicate, Retrievals.credentials and Retrievals.authorisedEnrolments)

      Mockito.verify(mockAuthConnector).authorise(eqTo(EmptyPredicate),
        eqTo(Retrievals.credentials and Retrievals.authorisedEnrolments))(any(), any())

       val authRetrievals = new AuthRetrievals(mockAuthConnector)
       val result = authRetrievals.refine(fakeRequest)
    }
  }
}
