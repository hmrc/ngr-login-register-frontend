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

import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.{FakeRequest, Injecting}
import play.mvc.Controller
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment, Enrolments}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

import scala.concurrent.Future
class AuthRetrievalsSpec extends TestSupport with GuiceOneAppPerSuite with Injecting {

  type Retrievals = Option[Credentials] ~ Option[String]  ~ Option[Name]
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val retrievals: Retrieval[Retrievals] = Retrievals.credentials and Retrievals.email and Retrievals.name
  val applicationConfig: AppConfig = injector().instanceOf[AppConfig]

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .overrides(
      api.inject.bind[AuthConnector].toInstance(mockAuthConnector),
    ).configure(
    "metrics.jvm" -> false
  ).build()

  val loginController = app.injector.instanceOf[AuthRetrievals]


  class FakeController extends Controller {
    def onPageLoad(): Action[AnyContent] = loginController {
      implicit request => Ok(request.nino.nino)
    }
  }

  "calling start" should {
    "return 200 for successful retrieval of 'authProviderId' from auth-client" in {
      val retrievalResult = Future.successful(
        new ~(new ~(GGCredId("cred-1234")), Enrolments(Set(Enrolment("IR-SA"))))
      )
      def controller = new TestLoginController(retrievalResult)
      val result = controller.start()(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }
}
