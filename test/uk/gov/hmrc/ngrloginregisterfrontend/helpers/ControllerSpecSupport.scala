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

import org.mockito.Mockito.when
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.{NgrFindAddressRepo, RatepayerRegistraionRepo}
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.utils.NGRLogger

import scala.concurrent.{ExecutionContext, Future}

trait ControllerSpecSupport extends TestSupport with TestData {

  implicit lazy val msgs: Messages = MessagesImpl(Lang("en"), inject[MessagesApi])
  val mockRatepayerRegistraionRepo: RatepayerRegistraionRepo = mock[RatepayerRegistraionRepo]
  val mockAuthJourney: AuthRetrievals = mock[AuthRetrievals]
  val mockIsRegisteredCheck: RegistrationAction = mock[RegistrationAction]
  val mockHasMandotoryDetailsAction: HasMandotoryDetailsAction  = mock[HasMandotoryDetailsAction]
  val mockComposedAction:ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent]  = mock[ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent]]


  val mockNgrFindAddressRepo: NgrFindAddressRepo = mock[NgrFindAddressRepo]
  val mockNGRConnector: NGRConnector = mock[NGRConnector]
  val mockSessionManager: SessionManager = mock[SessionManager]
  val mockNGRLogger: NGRLogger = mock[NGRLogger]
  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  mockRequest()

  def mockRequest(
                   credId: String = "1234",
                   hasNino: Boolean = true
                 ): Unit = {
    val testNino = if (hasNino) Some(Nino("AA000003D")) else None
    val updatedModel = testRegistrationModel.copy(nino = testNino)
    val finalActionBuilder = new ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] {
      override def invokeBlock[A](
                                   request: Request[A],
                                   block: RatepayerRegistrationValuationRequest[A] => Future[Result]
                                 ): Future[Result] = {
        val fakeReq = RatepayerRegistrationValuationRequest(
          request = request,
          credId = CredId(credId),
          ratepayerRegistration = Some(updatedModel)
        )
        println("mockAuthJourney 2 = " + fakeReq)
        block(fakeReq)
      }
      override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
      override protected def executionContext: ExecutionContext = ec
    }
    val mockAuthThenReg = mock[ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent]]
    when(mockAuthJourney.andThen(mockIsRegisteredCheck)).thenReturn(mockAuthThenReg)
    when(mockAuthThenReg.andThen(mockHasMandotoryDetailsAction)).thenReturn(finalActionBuilder)
  }

  def mockRequestNoMandotoryCheck(): Unit = {
    val finalActionBuilder = new ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] {
      override def invokeBlock[A](
                                   request: Request[A],
                                   block: RatepayerRegistrationValuationRequest[A] => Future[Result]
                                 ): Future[Result] = {
        val fakeReq = RatepayerRegistrationValuationRequest(
          request = request,
          credId = CredId("1234"),
          ratepayerRegistration = Some(testRegistrationModel)
        )
        println("mockAuthJourney 2 = " + fakeReq)
        block(fakeReq)
      }
      override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
      override protected def executionContext: ExecutionContext = ec
    }
    val mockAuthThenReg = mock[ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent]]
    when(mockAuthJourney.andThen(mockIsRegisteredCheck)).thenReturn(finalActionBuilder)
  }
}
