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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.{NINO, SAUTR}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo
import uk.gov.hmrc.ngrloginregisterfrontend.utils.{StringHelper, SummaryListHelper}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(view: CheckYourAnswersView,
                                           isRegisteredCheck: RegistrationAction,
                                           hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                           mongo: RatepayerRegistrationRepo,
                                           authenticate: AuthRetrievals,
                                           ngrConnector: NGRConnector,
                                           mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SummaryListHelper with StringHelper {

  def show(): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      val credId = CredId(request.credId.value)
      val optionRatepayer:Option[RatepayerRegistration] =  request.ratepayerRegistration
      val name = optionRatepayer.flatMap(_.name).map(_.value).getOrElse("")

      Future.successful(Ok(view(
        createContactDetailSummaryRows(ratepayerRegistrationValuation = RatepayerRegistrationValuation(credId,optionRatepayer), mode= "CYA", classes ="govuk-!-margin-bottom-9"),
        createTRNSummaryRows(RatepayerRegistrationValuation(credId, optionRatepayer)),
        name)))
    }

  def submit(): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      val credId = CredId(request.credId.value)
      val ratepayerData = request.ratepayerRegistration.map(_.copy(isRegistered = Some(true)))
      ngrConnector.upsertRatepayer(RatepayerRegistrationValuation(credId, ratepayerData)).flatMap { response =>
        if (response.status == CREATED) {
          mongo.deleteRecord(credId).map { _ =>
            Redirect(routes.RegistrationCompleteController.show(Some("234567")))
          }.recoverWith { case ex =>
            Future.failed(new Exception(s"Upsert succeeded, but failed to delete mongo record for $credId", ex))
          }
        } else {
          Future.failed(new Exception("Failed upsert to backend"))
        }
      }
    }

  private[controllers] def createTRNSummaryRows(ratepayerRegistrationValuation: RatepayerRegistrationValuation)(implicit messages: Messages): SummaryList = {
    def getUrl(linkId: String, messageKey: String): Option[Link] =
      Some(Link(Call("GET", routes.ConfirmUTRController.show.url), linkId, messageKey))

    val provideYourTRNRow: NGRSummaryListRow = NGRSummaryListRow(messages("checkYourAnswers.sautr"), None, Seq.empty, getUrl("sautr-linkid", "checkYourAnswers.add"))

    val ngrSummaryListRow: NGRSummaryListRow = ratepayerRegistrationValuation.ratepayerRegistration
      .flatMap(_.trnReferenceNumber)
      .map(trnReferenceNumber => trnReferenceNumber.referenceType match {
        case NINO =>
          NGRSummaryListRow(messages("checkYourAnswers.nino"), None, Seq(maskNino(trnReferenceNumber.value)), getUrl("nino-linkid", "checkYourAnswers.change"))
        case SAUTR if trnReferenceNumber.value.nonEmpty =>
          NGRSummaryListRow(messages("checkYourAnswers.sautr"), None, Seq(maskSAUTR(trnReferenceNumber.value)), getUrl("sautr-linkid", "checkYourAnswers.change"))
        case _ => provideYourTRNRow
      })
      .getOrElse(provideYourTRNRow)

      SummaryList(rows = Seq(summarise(ngrSummaryListRow)), classes = "govuk-!-margin-bottom-9")
  }
}
