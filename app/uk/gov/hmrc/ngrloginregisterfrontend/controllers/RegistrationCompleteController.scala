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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.RegistrationCompleteView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}


class RegistrationCompleteController @Inject()(view: RegistrationCompleteView,
                                               isRegisteredCheck: RegistrationAction,
                                               hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                               authenticate: AuthRetrievals,
                                               ngrConnector: NGRConnector,
                                               mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)extends FrontendController(mcc) with I18nSupport {


  def show(recoveryId: Option[String]): Action[AnyContent] =
    authenticate.async { implicit request =>
      val credId = request.credId
      ngrConnector.getRatepayer(credId).flatMap {
        case Some(ratepayer) =>
          val email = ratepayer.ratepayerRegistration.flatMap(_.email).map(_.value).getOrElse("")
          Future.successful(Ok(view(recoveryId, email)))
        case None =>
          Future.failed(new RuntimeException("Can not find ratepayer email in the database"))
      }
  }

  def submit(recoveryId: Option[String]) : Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async {
      Future.successful(Redirect(routes.StartController.show))
    }

}
