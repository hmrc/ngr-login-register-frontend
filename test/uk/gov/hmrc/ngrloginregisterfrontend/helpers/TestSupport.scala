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

package uk.gov.hmrc.ngrloginregisterfrontend.helpers

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.{AffinityGroup, ConfidenceLevel, Nino}
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockAppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddressWrapper

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait TestSupport extends PlaySpec
with GuiceOneAppPerSuite
with TestData
with Matchers
with MockitoSugar
with Injecting
with BeforeAndAfterEach
with ScalaFutures
with IntegrationPatience {
  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    GuiceApplicationBuilder()
      .overrides()

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  override implicit lazy val app: Application = localGuiceApplicationBuilder().build()

  lazy val cacheId = "id"

  lazy val testCredId: Credentials = Credentials(providerId = "0000000022", providerType = "Government-Gateway")
  lazy val testNino: String = "AA000003D"
  lazy val testConfidenceLevel: ConfidenceLevel = ConfidenceLevel.L250
  lazy val testEmail: String = "user@test.com"
  lazy val testAffinityGroup: AffinityGroup = AffinityGroup.Individual
  lazy val testName: Name = Name(name = Some("testUser"), lastName = Some("testUserLastName"))

  private lazy val addressResponseKey: String = "Address-Lookup-Response"
  private val addressLookupResponsesJson: JsValue = Json.parse(
    """
      |[
      | {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | }
      |]
      |""".stripMargin
  )

  private val addressLookupResponses: Seq[LookedUpAddressWrapper] = addressLookupResponsesJson.as[Seq[LookedUpAddressWrapper]]
  private val expectAddressesJsonString = Json.toJson(addressLookupResponses.map(_.address)).toString()
  def injector: Injector = app.injector
  lazy val frontendAppConfig: AppConfig = inject[AppConfig]
  lazy val messagesApi: MessagesApi             = inject[MessagesApi]
  lazy val languageUtils: LanguageUtils         = inject[LanguageUtils]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type]                                                =
    FakeRequest("", "").withHeaders(HeaderNames.authorisation -> "Bearer 1")

  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val authenticatedFakeRequest: AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, None, nino = Nino(true, Some("")))

  lazy val authenticatedFakeRequestWithEmail: AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    AuthenticatedUserRequest(fakeRequest, None, None, None, Some("testEmail@emailProvider.com"), None, None, nino = Nino(true, Some("")))

  lazy val authenticatedFakeRequestNoNino: AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, None, nino = Nino(false, None))

  lazy val authenticatedFakeRequestWithSession: AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    AuthenticatedUserRequest(fakeRequest.withSession(addressResponseKey -> expectAddressesJsonString), None, None, None, None, None, None, nino = Nino(true, Some("")))

  implicit lazy val messages: Messages = MessagesImpl(Lang("en"), messagesApi)

  def await[A](f: Future[A]): A = Await.result(f, 5.seconds)

  lazy val mcc: MessagesControllerComponents = inject[MessagesControllerComponents]

  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]
  implicit val hc: HeaderCarrier         = HeaderCarrier()
  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)

}
