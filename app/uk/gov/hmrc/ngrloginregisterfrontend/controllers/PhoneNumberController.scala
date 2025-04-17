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
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.PhoneNumber.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.PhoneNumber
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.PhoneNumberView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PhoneNumberController @Inject()(
                                       phoneNumberView: PhoneNumberView,
                                       connector: NGRConnector,
                                       authenticate: AuthJourney,
                                       mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(mode: String): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      connector.getRatepayer(CredId(request.credId.getOrElse(""))).flatMap { ratepayerRegistrationValuation =>
        ratepayerRegistrationValuation.flatMap(_.ratepayerRegistration).flatMap(
          contactNumber => contactNumber.contactNumber.map(
          number =>
          Future.successful(Ok(phoneNumberView(form().fill(PhoneNumber(number.value)), mode))
        ))).getOrElse(Future.successful(Ok(phoneNumberView(form(), mode))))
      }
    }
  }


  def submit(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      PhoneNumber.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(phoneNumberView(formWithErrors, mode))),
          phoneNumber => {
            connector.changePhoneNumber(CredId(request.credId.getOrElse("")), PhoneNumber(phoneNumber.value))
            if (mode.equals("CYA"))
              Future.successful(Redirect(routes.CheckYourAnswersController.show))
            else
              Future.successful(Redirect(routes.ConfirmContactDetailsController.show(None)))
          }
        )
    }
}
