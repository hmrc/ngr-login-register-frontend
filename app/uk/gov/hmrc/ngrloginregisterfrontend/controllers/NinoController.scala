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
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.models.Nino.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.NINO
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NinoController @Inject()(
                                ninoView: NinoView,
                                connector: NGRConnector,
                                authenticate: AuthJourney,
                                mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      val authNino = request.nino.nino.getOrElse(throw new RuntimeException("No nino found from auth"))
      connector.getRatepayer(CredId(request.credId.getOrElse(""))).map {
        case Some(ratepayer) =>
          val ninoForm: Option[Form[Nino]] = for {
            ratepayer <- ratepayer.ratepayerRegistration
            trnReferenceNumber <- ratepayer.trnReferenceNumber.filter(_.referenceType == NINO)
          } yield form(authNino).fill(Nino(trnReferenceNumber.value))

          Ok(ninoView(ninoForm.getOrElse(form(request.nino.nino.get))))

        case None =>
          Ok(ninoView(form(request.nino.nino.get)))
      }
    }
  }

  def submit(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      val authNino = request.nino.nino.getOrElse(throw new RuntimeException("No nino found from auth"))
      Nino.form(authNino)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(ninoView(formWithErrors))),
          nino => {
            connector.changeTrn(CredId(request.credId.getOrElse("")), TRNReferenceNumber(NINO, nino.value))
            Future.successful(Redirect(routes.CheckYourAnswersController.show))
          }
        )
    }
}

