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
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthRetrievals,
                                                isRegisteredCheck: RegistrationAction,
                                                hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                                connector: NGRConnector,
                                                mcc: MessagesControllerComponents,
                                                )(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SummaryListHelper {

  def show(): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      val credId = CredId(request.credId.value)
      val name = request.ratepayerRegistration.flatMap(details => details.name.map(name => name.value)).getOrElse("")
      val ratepayerData = RatepayerRegistrationValuation(credId, request.ratepayerRegistration)
      connector.upsertRatepayer(ratepayerData).map { _ =>
        Ok(view(createContactDetailSummaryRows(ratepayerData, "CCD"), name))
      }
    }
  }

  def submit(): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async {
      Future.successful(Redirect(routes.ProvideTRNController.show()))
    }
  }
}
