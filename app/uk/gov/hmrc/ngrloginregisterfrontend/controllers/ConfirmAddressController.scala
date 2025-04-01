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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Session}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmAddressForm.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ConfirmAddressController @Inject()(confirmAddressView: ConfirmAddressView,
                                         authenticate: AuthJourney,
                                         sessionManager: SessionManager,
                                         connector: NGRConnector,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {
  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-address-radio"), Seq(yesButton, noButton))
  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      Future.successful(Ok(confirmAddressView(getAddressFromSession(request.session), form, buildRadios(form, ngrRadio))))
    }

  def submit(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(confirmAddressView(getAddressFromSession(request.session), formWithErrors, buildRadios(formWithErrors, ngrRadio)))),
          confirmAddressForm => {
            if (confirmAddressForm.radioValue.equals("Yes")) {
              sessionManager.getSessionValue(request.session, sessionManager.chosenAddressIdKey)
                .map(Json.parse(_).as[Address])
                .map(connector.changeAddress(CredId(request.credId.getOrElse("")), _))
              Future.successful(Redirect(routes.ConfirmContactDetailsController.show))
            }else{
              Future.successful(Redirect(routes.FindAddressController.show))
            }
          }
        )
    }

  private def getAddressFromSession(session: Session): String =
    sessionManager.getSessionValue(session, sessionManager.chosenAddressIdKey)
    .map(Json.parse(_).as[Address])
    .map(address => s"${address.line1}, ${if (address.line2.isDefined) address.line2.get + "," else ""} ${address.town} ${address.postcode}")
    .getOrElse("")

}
