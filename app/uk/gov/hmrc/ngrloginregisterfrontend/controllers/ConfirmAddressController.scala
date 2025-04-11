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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmAddressForm.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SessionTimeoutHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmAddressController @Inject()(confirmAddressView: ConfirmAddressView,
                                         authenticate: AuthJourney,
                                         sessionManager: SessionManager,
                                         connector: NGRConnector,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SessionTimeoutHelper {
  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-address-radio"), Seq(yesButton, noButton))
  def show(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      getAddressFromSession match {
        case Right(addressOpt) => Future.successful(Ok(confirmAddressView(getChosenAddressString(addressOpt), form, buildRadios(form, ngrRadio), mode)))
        case Left(result) => Future.successful(result)
      }
    }

  def submit(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      def redirectPage(mode: String): Result = if (mode == "CYA") Redirect(routes.CheckYourAnswersController.show) else Redirect(routes.ConfirmContactDetailsController.show)
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            getAddressFromSession match {
              case Right(addressOpt) => Future.successful(BadRequest(confirmAddressView(getChosenAddressString(addressOpt), formWithErrors, buildRadios(formWithErrors, ngrRadio), mode)))
              case Left(result) => Future.successful(result)
            },
          confirmAddressForm => {
            val result:Result = if (confirmAddressForm.radioValue.equals("Yes")) {
              updateAddress().fold(result => result, _ => redirectPage(mode))
            } else {
              redirectPage(mode)
            }
            Future.successful(result)
          }
        )
    }

  private def updateAddress()(implicit request: AuthenticatedUserRequest[_]): Either[Result, Unit] =
    getAddressFromSession match {
        case Right(addressOpt) =>
          addressOpt
            .map(connector.changeAddress(CredId(request.credId.getOrElse("")), _))
            .map(_ => Right())
            .getOrElse(Right())
        case Left(result) => Left(result)
      }

  private def getChosenAddressString(addressOpt: Option[Address]): String =
    addressOpt
    .map(address => s"${address.line1}, ${address.line2.map(line2 => s"$line2,").getOrElse("")} ${address.town} ${address.postcode}")
    .getOrElse("")

  private def getAddressFromSession()(implicit request: AuthenticatedUserRequest[_]): Either[Result, Option[Address]] =
    getSession(sessionManager, request.session, sessionManager.chosenAddressIdKey)
      .fold(result => Left(result), addressStrOpt => Right(addressStrOpt.map(Json.parse(_).as[Address])))


}
