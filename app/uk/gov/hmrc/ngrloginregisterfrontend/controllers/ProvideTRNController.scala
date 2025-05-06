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
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ProvideTRNView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ProvideTRNController @Inject()(view: ProvideTRNView,
                                     isRegisteredCheck: RegistrationAction,
                                     authenticate: AuthRetrievals,
                                     mcc: MessagesControllerComponents)(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] =
    (isRegisteredCheck andThen authenticate){ implicit request =>
      Ok(view())
    }

  def submit() : Action[AnyContent] =
    (isRegisteredCheck andThen authenticate) {
      Redirect(routes.ConfirmUTRController.show)
    }

}
