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
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, _}
import uk.gov.hmrc.govukfrontend.views.Aliases.Action
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationActionImpl @Inject()(
                                    ngrConnector: NGRConnector,
                                    authenticate: AuthJourney,
                                    appConfig: AppConfig,
                                    mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)  extends  RegistrationAction{

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    authenticate.authWithUserDetails.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val credId = CredId(request.credId.getOrElse(""))
    val isRegisteredResult: Future[Boolean] = ngrConnector.getRatepayer(credId).map { ratepayerValuationOpt =>
      // Navigate through the nested Options to get to isRegistered
      ratepayerValuationOpt
        .flatMap(_.ratepayerRegistration)
        .flatMap(_.isRegistered)
        .getOrElse(false)
    }

    isRegisteredResult.map { isRegistered =>
      isRegistered match
      case true => redirectToDashboard
      case _ => redirectToDashboard
    }

  }

  def redirectToDashboard(): Future[Result] = {
    Future.successful(Redirect(routes.StartController.show))
  }

  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec


}

@ImplementedBy(classOf[RegistrationActionImpl])
trait RegistrationAction extends ActionBuilder[Request, AnyContent] with ActionFunction[Request, Request]
