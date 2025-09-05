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

/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrloginregisterfrontend.actions

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import play.api.Application
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo

import scala.concurrent.Future

class HasMandotoryDetailsActionSpec extends TestSupport with TestData{

  override implicit lazy val app: Application = GuiceApplicationBuilder().build()
  private val mockNGRConnector: NGRConnector = mock[NGRConnector]
  private val mockRegistrationAction: RegistrationAction = mock[RegistrationAction]
  private val mockRatepayerRegistraionRepo: RatepayerRegistrationRepo = mock[RatepayerRegistrationRepo]
  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockAuthAction = new AuthRetrievalsImpl(mockAuthConnector, mcc)

  private val mockCitizenDetailsConnector:CitizenDetailsConnector = mock[CitizenDetailsConnector]

  val hasMandotoryDetailsAction = new HasMandotoryDetailsActionImpl(
    mongo = mockRatepayerRegistraionRepo,
    isRegistered = mockRegistrationAction,
    mcc)

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

  "Has mandatory details action" when {
    val testBlock: RatepayerRegistrationValuationRequest[AnyContent] => Future[Result] =
      _ => Future.successful(Results.Ok("Passed"))

    when(mockRegistrationAction.invokeBlock(
      any[Request[AnyContent]],
      any[RatepayerRegistrationValuationRequest[AnyContent] => Future[Result]]
    )).thenAnswer {
      (invocation: InvocationOnMock) =>
        val block =
          invocation
            .getArgument(1)
            .asInstanceOf[RatepayerRegistrationValuationRequest[AnyContentAsEmpty.type] => Future[Result]]

        val fakeRefinedRequest = RatepayerRegistrationValuationRequest(
          request = FakeRequest(),
          credId = CredId("1234"),
          ratepayerRegistration = Some(testRegistrationModel)
        )

        block(fakeRefinedRequest)
    }

    "all mandatory details present" should {
      "call block if both email and contactNumber are present" in {
        when(mockRatepayerRegistraionRepo.findByCredId(any())).thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel)))))

        val result = hasMandotoryDetailsAction.invokeBlock(fakeRequest, testBlock)
        status(result) mustBe OK
      }
    }
  }
}
