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

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.audit.AuditModel
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Nino.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.NINO
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo
import uk.gov.hmrc.ngrloginregisterfrontend.services.AuditingService
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NinoController @Inject()(
                                ninoView: NinoView,
                                mongo: RatepayerRegistrationRepo,
                                isRegisteredCheck: RegistrationAction,
                                hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                authenticate: AuthRetrievals,
                                mcc: MessagesControllerComponents, auditingService: AuditingService)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      val authNino = request.ratepayerRegistration.flatMap{ ratePayer =>
        ratePayer match {
          case ratePayer if ratePayer.nino.isDefined == true => ratePayer.nino
          case _ => throw new RuntimeException("No nino found from auth")
        }
      }.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))

      mongo.findByCredId(CredId(request.credId.value)).map {
        case Some(ratepayer) =>
          val ninoForm: Option[Form[Nino]] = for {
            ratepayer <- ratepayer.ratepayerRegistration
            trnReferenceNumber <- ratepayer.trnReferenceNumber.filter(_.referenceType == NINO)
          } yield form(authNino.value).fill(Nino(trnReferenceNumber.value))

          Ok(ninoView(ninoForm.getOrElse(form(authNino.nino))))

        case None =>
          Ok(ninoView(form(authNino.nino)))
      }
    }
  }

  def submit(): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      val authNino = request.ratepayerRegistration.flatMap{ ratePayer =>
        ratePayer match {
          case ratePayer if ratePayer.nino.isDefined == true => ratePayer.nino
          case _ => throw new RuntimeException("No nino found from auth")
        }
      }.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))
      Nino.form(authNino.nino)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(ninoView(formWithErrors))),
          nino => {
            auditingService.extendedAudit(AuditModel(request.credId.value, "check-answers", nino = nino.value),
              uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.NinoController.show.url)
            mongo.updateTRN(CredId(request.credId.value), TRNReferenceNumber(NINO, nino.value))
            Future.successful(Redirect(routes.CheckYourAnswersController.show))
          }
        )
    }
}

