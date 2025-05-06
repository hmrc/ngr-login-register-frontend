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
import com.google.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email.form
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class EnterEmailController @Inject()(view: EmailView,
                                     mcc: MessagesControllerComponents,
                                     isRegisteredCheck: RegistrationAction,
                                     authenticate: AuthRetrievals)
                                    (implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {
  def show(): Action[AnyContent] =
    (isRegisteredCheck andThen authenticate).async { implicit request =>
      Future.successful(Ok(view(form(), "enterEmail")))
    }

  def submit(): Action[AnyContent] = {
    (isRegisteredCheck andThen authenticate).async { implicit request =>
      form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, "enterEmail"))),
          email => {
            Future.successful(Redirect(routes.ConfirmContactDetailsController.show(Some(email.value))))
          }
        )
    }
  }

}
