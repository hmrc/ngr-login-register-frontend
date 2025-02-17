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

import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.{createSummaryRows, summarise}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthJourney,
                                                mcc: MessagesControllerComponents,
                                                citizenDetailsConnector: CitizenDetailsConnector)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      citizenDetailsConnector.getPersonDetails(Nino(request.nino.nino.getOrElse(""))).map {
        case Left(error) => Status(error.code)(Json.toJson(error))
        case Right(personDetails) => Ok(view(SummaryList(createSummaryRows(personDetails, request))))
      }
    }


}
