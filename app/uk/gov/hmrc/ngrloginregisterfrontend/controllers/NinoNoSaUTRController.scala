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
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NinoNoSaUTR.{NoLater, Yes, form}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.NINO
import uk.gov.hmrc.ngrloginregisterfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName, NinoNoSaUTR}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoNoSaUTRView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NinoNoSaUTRController @Inject()(ninoNoSaUTRView: NinoNoSaUTRView,
                                      authenticate: AuthJourney,
                                      connector: NGRConnector,
                                      mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport{

  def showNinoNoSaUTR: Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      val authNino = request.nino.nino.getOrElse(throw new Exception("No nino found from auth"))
      connector.getRatepayer(CredId(request.credId.getOrElse(""))).map {
        case Some(_) =>
          val ninoNoSaUTRForm = form(Some(authNino))
          val validatedForm = NinoNoSaUTR.validateForm(ninoNoSaUTRForm)
          Ok(ninoNoSaUTRView(validatedForm,radios(validatedForm)))
        case None =>
          Ok(ninoNoSaUTRView(form(Some(authNino)),radios(form(Some(authNino)))))
      }
    }
  }

  private def radios[A](form: Form[A])(implicit  messages: Messages): Radios = {
    NGRRadio.buildRadios(form = form, NGRRadios = NGRRadio(
      radioGroupName = NGRRadioName(NinoNoSaUTR.formName),
      NGRRadioButtons = Seq(
        NGRRadioButtons(radioContent = messages("confirmTrn.yesProvide"), radioValue = Yes),
        NGRRadioButtons(radioContent = messages("confirmTrn.noNI"), radioValue = NoLater),
      ),
      ngrTitle = None
    ))
  }


    def submitNinoNoSaUTR(): Action[AnyContent] =
      authenticate.authWithUserDetails.async { implicit request =>
        request.nino.nino match {
          case None => Future.failed(new Exception())
          case Some(authNino) =>
            val boundForm = NinoNoSaUTR.form(Some(authNino))
              .bindFromRequest()
              NinoNoSaUTR.validateForm(boundForm)
              .fold(
                formWithErrors => {
                  Future.successful(BadRequest(ninoNoSaUTRView(formWithErrors, radios(formWithErrors))))
                },
                {
                  //TODO Need to change the routes once the other pages are merged
                  case NinoNoSaUTR(nino, Yes) =>
                    connector.changeTrn(CredId(request.credId.getOrElse("")), TRNReferenceNumber(NINO, nino.getOrElse("")))
                    Future.successful(Redirect(routes.ConfirmContactDetailsController.show))
                  case NinoNoSaUTR(None, NoLater)  =>
                    connector.changeTrn(CredId(request.credId.getOrElse("")), TRNReferenceNumber(NINO, ""))
                    Future.successful(Redirect(routes.ConfirmContactDetailsController.show))
                  case _ => Future.failed(new Exception())
                }
              )
        }
      }






}
