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

import play.api.mvc.Request
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, Nino}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.config.ErrorHandler
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class AuthRetrievals @Inject()(
                               val authConnector: AuthConnector,
                               errorHandler: ErrorHandler
                              )(implicit ec: ExecutionContext)  extends AuthorisedFunctions {
  def refine[A](request: Request[A]): Future[AuthenticatedUserRequest[A]]= {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    println(Console.CYAN_B + ("BEFORE AUTH") + Console.RESET)
    authorised().retrieve(
      Retrievals.credentials and
      Retrievals.nino and
      Retrievals.confidenceLevel and
      Retrievals.email and
      Retrievals.affinityGroup and
      Retrievals.name
    ) {
        case credentials ~ Some(nino) ~ confidenceLevel ~ email ~ affinityGroup ~ name  if confidenceLevel.level >= 250 =>
          Future.successful(
              AuthenticatedUserRequest(
                request = request,
                confidenceLevel = Some(confidenceLevel),
                authProvider = credentials.map(_.providerType),
                nino = Nino(hasNino = true,Some(nino)),
                email = email,
                credId = credentials.map(_.providerId),
                affinityGroup = affinityGroup,
                name = name
              )
          )
        case _ ~ _ ~ confidenceLevel ~ _ => throw new Exception("confidenceLevel not met")
        case _ =>  throw new Exception("Nino not found")
      }
  }
}