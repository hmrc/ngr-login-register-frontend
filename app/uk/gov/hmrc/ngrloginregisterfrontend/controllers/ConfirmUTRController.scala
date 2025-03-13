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
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmUTRView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmUTRController @Inject()(view: ConfirmUTRView,
                                     authenticate: AuthJourney,
                                     citizenDetailsConnector: CitizenDetailsConnector,
                                     mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      val nino = Nino(request.nino.nino.getOrElse(""))
      citizenDetailsConnector.getMatchingResponse(nino).flatMap {
        case Left(error) => Future.failed(new RuntimeException(s"call to citizen details failed: ${error.code} ${error.message}"))
        case Right(details) =>
          details.saUtr
            .map(_.value)
            .map(utr => Future.successful(Ok(view(utr))))
            .getOrElse(Future.failed(new RuntimeException("No SAUTR found")))
      }

    }

}
