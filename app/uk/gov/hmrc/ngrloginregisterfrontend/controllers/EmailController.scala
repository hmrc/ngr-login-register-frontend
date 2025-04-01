/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmailController @Inject()(emailView: EmailView,
                                connector: NGRConnector,
                                authenticate: AuthJourney,
                                mcc: MessagesControllerComponents,
                               )(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show(mode: String): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      connector.getRatepayer(CredId(request.credId.getOrElse(""))).map { ratepayerOpt =>
        val emailForm = ratepayerOpt
          .flatMap(_.ratepayerRegistration)
          .flatMap(_.email)
          .map(email => form().fill(Email(email.value)))
          .getOrElse(form())

        Ok(emailView(emailForm, mode))
      }
    }
  }

  def submit(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      Email.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(emailView(formWithErrors, mode))),
          email => {
            connector.changeEmail(CredId(request.credId.getOrElse("")), email)
            if (mode.equals("CYA"))
              Future.successful(Redirect(routes.CheckYourAnswersController.show))
            else
              Future.successful(Redirect(routes.ConfirmContactDetailsController.show))
          }
        )
    }

}