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

import com.google.inject.ImplementedBy
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~, Name => authName}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.models.RatepayerRegistration
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Email, Nino}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthRetrievalsImpl @Inject()(
                               val authConnector: AuthConnector,
                               mcc: MessagesControllerComponents,
                              )(implicit ec: ExecutionContext) extends AuthRetrievals
  with AuthorisedFunctions {

  type RetrievalsType = Option[Credentials] ~ Option[String] ~ ConfidenceLevel ~ Option[String] ~ Option[AffinityGroup] ~ Option[authName]

  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals: Retrieval[RetrievalsType] =
      Retrievals.credentials and
      Retrievals.nino and
      Retrievals.confidenceLevel and
      Retrievals.email and
      Retrievals.affinityGroup and
      Retrievals.name

     authorised(ConfidenceLevel.L250).retrieve(retrievals){
        case credentials ~ Some(nino) ~ confidenceLevel ~ email ~ affinityGroup ~ name =>
          block(
            RatepayerRegistrationValuationRequest(
              request = request,
              credId = CredId(credentials.map(_.providerId).getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))),
              ratepayerRegistration = Some(RatepayerRegistration(
                nino = Some(Nino(nino)),
                name = None,
                email = if(email.filter(_.nonEmpty).isEmpty){None}else Some(Email(email.getOrElse("")))
              ))
            )
          )
        case _ ~ _ ~ confidenceLevel ~ _ => throw new Exception("confidenceLevel not met")
      }recoverWith {
      case ex: Throwable =>
        throw ex
    }
  }
  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$
}

@ImplementedBy(classOf[AuthRetrievalsImpl])
trait AuthRetrievals extends ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] with ActionFunction[Request, RatepayerRegistrationValuationRequest]