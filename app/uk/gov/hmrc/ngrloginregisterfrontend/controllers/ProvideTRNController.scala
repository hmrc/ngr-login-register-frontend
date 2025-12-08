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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.audit.AuditModel
import uk.gov.hmrc.ngrloginregisterfrontend.services.AuditingService
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ProvideTRNView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ProvideTRNController @Inject()(view: ProvideTRNView,
                                     isRegisteredCheck: RegistrationAction,
                                     hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                     authenticate: AuthRetrievals,
                                     mcc: MessagesControllerComponents, auditingService: AuditingService)(implicit appConfig: AppConfig, ex: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction){ implicit request =>
      Ok(view())
    }

  def submit() : Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction) { request =>
      implicit val hc: HeaderCarrier =
        HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      auditingService.extendedAudit(AuditModel(request.credId.value,"confirm-utr"),
        uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.ProvideTRNController.show().url)
      Redirect(routes.ConfirmUTRController.show)
    }

}
