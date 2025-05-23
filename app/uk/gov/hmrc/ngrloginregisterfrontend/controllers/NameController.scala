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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Name
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Name.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NameView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NameController  @Inject()(
                                 nameView: NameView,
                                 mongo: RatepayerRegistrationRepo,
                                 isRegisteredCheck: RegistrationAction,
                                 hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                 authenticate: AuthRetrievals,
                                 mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(mode: String): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request:RatepayerRegistrationValuationRequest[AnyContent] =>
      mongo.findByCredId(CredId(request.credId.value)).map { ratepayerOpt =>
        val nameForm = ratepayerOpt
          .flatMap(_.ratepayerRegistration)
          .flatMap(_.name)
          .map(name => form().fill(Name(name.value)))
          .getOrElse(form())
        Ok(nameView(nameForm, mode))
      }
    }
  }

  def submit(mode: String): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      Name.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(nameView(formWithErrors, mode))),
          name => {
            mongo.updateName(CredId(request.credId.value), name)
            if (mode.equals("CYA"))
              Future.successful(Redirect(routes.CheckYourAnswersController.show))
            else
              Future.successful(Redirect(routes.ConfirmContactDetailsController.show()))
          }
        )
    }
}
