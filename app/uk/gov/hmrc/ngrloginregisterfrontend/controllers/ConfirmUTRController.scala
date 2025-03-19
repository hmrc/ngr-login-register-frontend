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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmUTR
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmUTR.{NoLater, NoNI, Yes}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName, NGRSummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmUTRView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import ConfirmUTR.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.SAUTR
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, ReferenceNumber}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmUTRController @Inject()(view: ConfirmUTRView,
                                     authenticate: AuthJourney,
                                     citizenDetailsConnector: CitizenDetailsConnector,
                                     NGRConnector: NGRConnector,
                                     mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  private var savedUtr: String = ""

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      request.nino.nino match {
        case Some(nino) =>
          citizenDetailsConnector.getMatchingResponse(Nino(nino)).flatMap {
            case Left(error) => Future.failed(new RuntimeException(s"call to citizen details failed: ${error.code} ${error.message}"))
            case Right(details) =>
              details.saUtr
                .map(utr => {
                  savedUtr = utr.value
                  Future.successful(Ok(view(form(), summaryList(maskString(savedUtr)), radios())))
                })
                .getOrElse(Future.failed(new RuntimeException("No SAUTR found")))
          }
        case None => Future.failed(new RuntimeException("No NINO found in request"))
      }
    }

  private[controllers] def summaryList(utr: String)(implicit messages: Messages): SummaryList = {
    SummaryList(Seq(
      NGRSummaryListRow.summarise(
        NGRSummaryListRow(
          titleMessageKey = Messages("confirmUtr.sautr"),
          captionKey = None,
          value = Seq(utr),
          changeLink = None
        )
      )
    ))
  }

  private[controllers] def radios()(implicit  messages: Messages): Radios = {
    NGRRadio.buildRadios(form = form(), NGRRadios = NGRRadio(
      radioGroupName = NGRRadioName(ConfirmUTR.formName),
      NGRRadioButtons = Seq(
        NGRRadioButtons(radioContent = messages("confirmUtr.yesProvide"), radioValue = Yes(savedUtr)),
        NGRRadioButtons(radioContent = messages("confirmUtr.noNI"), radioValue = NoNI),
        NGRRadioButtons(radioContent = messages("confirmUtr.noLater"), radioValue = NoLater)
      ),
      ngrTitle = None
    ))
  }

  private def maskString(input: String): String =
    if (input.length < 3) input
    else "*".repeat(input.length - 3) + input.takeRight(3)

  def submit(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      ConfirmUTR.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, summaryList(maskString(savedUtr)), radios()))),
          utrChoice => {
            request.credId match {
              case Some(credId) =>
                utrChoice match {
                  case ConfirmUTR.Yes(utr) =>
                    println(s"Yes selected, UTR: $utr")
                    NGRConnector.changeTrn(CredId(credId), ReferenceNumber(SAUTR, utr))
                  case ConfirmUTR.NoNI => println("No, will provide NINO")
                  case ConfirmUTR.NoLater => println("No, will provide TRN later")
                }
              case None => Future.failed(new RuntimeException("No Cred ID found in request"))
            }
            //TODO: next page
            Future.successful(Redirect(routes.ConfirmContactDetailsController.show))
          }
        )
    }

}
