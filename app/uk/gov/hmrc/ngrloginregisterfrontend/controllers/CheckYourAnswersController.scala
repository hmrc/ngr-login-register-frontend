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
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.{NINO, SAUTR}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(view: CheckYourAnswersView,
                                           authenticate: AuthJourney,
                                           connector: NGRConnector,
                                           mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SummaryListHelper {

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))

      connector.getRatepayer(credId).flatMap {
        case Some(ratepayer) =>
          val name = ratepayer.ratepayerRegistration.flatMap(_.name).map(_.value).getOrElse("")
          Future.successful(Ok(view(createContactDetailSummaryRows(ratepayer), createTRNSummaryRows(ratepayer), name)))
        case None =>
          Future.failed(new RuntimeException(s"Can not find CredId: $credId in the database"))
      }
    }

//  def submit(): Action[AnyContent] =
//    Action.async { implicit request =>
//    }

  private def createTRNSummaryRows(ratepayerRegistrationValuation: RatepayerRegistrationValuation)(implicit messages: Messages): SummaryList = {
    def getUrl(route: String, linkId: String, messageKey: String): Option[Link] =
      Some(Link(Call("GET", route), linkId, messageKey))

    val ngrSummaryListRow: NGRSummaryListRow = ratepayerRegistrationValuation.ratepayerRegistration
      .flatMap(_.referenceNumber)
      .map(trnReferenceNumber => trnReferenceNumber.referenceType match {
        case NINO => NGRSummaryListRow(messages("checkYourAnswers.nino"), None, Seq(trnReferenceNumber.value), getUrl(routes.NameController.show.url, "nino-linkid", "Change"))
        case SAUTR => NGRSummaryListRow(messages("checkYourAnswers.sautr"), None, Seq(trnReferenceNumber.value), getUrl(routes.PhoneNumberController.show.url, "sautr-linkid",
          if (trnReferenceNumber.value.isEmpty) "checkYourAnswers.add" else "checkYourAnswers.change"))
      })
      .getOrElse(NGRSummaryListRow(messages("checkYourAnswers.sautr"), None, Seq.empty, getUrl(routes.PhoneNumberController.show.url, "sautr-linkid", "checkYourAnswers.add")))

      SummaryList(Seq(summarise(ngrSummaryListRow)))
  }
}
