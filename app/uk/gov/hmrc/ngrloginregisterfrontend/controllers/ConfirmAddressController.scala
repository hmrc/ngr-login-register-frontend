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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddress
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmAddressForm.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.repo.NgrFindAddressRepo
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmAddressController @Inject()(confirmAddressView: ConfirmAddressView,
                                         isRegisteredCheck: RegistrationAction,
                                          hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                         authenticate: AuthRetrievals,
                                         ngrFindAddressRepo: NgrFindAddressRepo,
                                         connector: NGRConnector,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {
  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-address-radio"), Seq(yesButton, noButton))
  def show(mode: String, index: Int): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      ngrFindAddressRepo.findChosenAddressByCredId(CredId(request.credId.value), index).flatMap {
        case None =>
          Future.successful(Redirect(routes.FindAddressController.show(mode)))
        case Some(address) =>
          Future.successful(Ok(confirmAddressView(address.toString, index, form, buildRadios(form, ngrRadio), mode)))
      }
    }

  def submit(mode: String, index: Int): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      def redirectPage(mode: String): Result = if (mode == "CYA") Redirect(routes.CheckYourAnswersController.show) else Redirect(routes.ConfirmContactDetailsController.show(None))

      ngrFindAddressRepo.findChosenAddressByCredId(CredId(request.credId.value), index).flatMap {
        case None =>
          Future.successful(Redirect(routes.FindAddressController.show(mode)))
        case Some(address) =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(BadRequest(confirmAddressView(address.toString, index, formWithErrors, buildRadios(formWithErrors, ngrRadio), mode))),
              confirmAddressForm => {
                if (confirmAddressForm.radioValue.equals("Yes"))
                  connector.changeAddress(CredId(request.credId.value), convertLookedUpAddressToNGRAddress(address))
                    .map(_ => redirectPage(mode))
                else
                  Future.successful(redirectPage(mode))
              }
            )
      }
    }

  private[controllers] def convertLookedUpAddressToNGRAddress(lookedUpAddress: LookedUpAddress): Address = {
    val splitIndex: Int = if (lookedUpAddress.lines.size % 2 > 0) lookedUpAddress.lines.size / 2 + 1 else lookedUpAddress.lines.size / 2
    val lineSeq = lookedUpAddress.lines.splitAt(splitIndex)
    val line1 = lineSeq._1.mkString(" ")
    val line2 = if (lineSeq._2.isEmpty) None else Some(lineSeq._2.mkString(" "))
    Address(line1, line2, lookedUpAddress.town, lookedUpAddress.county, Postcode(lookedUpAddress.postcode))
  }
}
