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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookup.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{Address, AddressLookupRequest, AddressLookupResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress.form
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FindAddressController @Inject()(findAddressView: FindAddressView,
                                      addressLookupConnector: AddressLookupConnector,
                                      sessionManager: SessionManager,
                                      logger: NGRLogger,
                                      authenticate: AuthJourney,
                                      mcc: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent]  = {
    authenticate.authWithUserDetails.async { implicit request =>
      Future.successful(Ok(findAddressView(form())))
    }
  }

  def submit(): Action[AnyContent] =
    Action.async { implicit request =>
      FindAddress.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(findAddressView(formWithErrors))),
          findAddress => {
            appConfig.getString("addressLookup.enabled") match {
              case "false" =>
                //TODO create dummy Address Seq
                val addresses: Seq[Address] = Seq.empty
                val addressLookupResponseSession = sessionManager.setAddressLookupResponse(request.session, addresses)
                //TODO calling the AddressSearchResultController
                Future.successful(Redirect(routes.ConfirmContactDetailsController.show).withSession(addressLookupResponseSession))
              case _ =>
                addressLookupConnector.findAddressByPostcode(AddressLookupRequest(findAddress.postcode.value, findAddress.propertyName))
                .flatMap(e  => e match {
                  case Right(responses: Seq[AddressLookupResponse]) =>
                    val addresses: Seq[Address] = responses.map(_.address)
                    val addressLookupResponseSession = sessionManager.setAddressLookupResponse(request.session, addresses)
                    //TODO calling the AddressSearchResultController
                    Future.successful(Redirect(routes.ConfirmContactDetailsController.show).withSession(addressLookupResponseSession))
                  case Left(errorResponse: ErrorResponse) =>
                    logger.error(s"AddressLookup has returned an error: status ${errorResponse.code}, ${errorResponse.message}")
                    Future.successful(InternalServerError(Json.toJson(errorResponse)))
                })
            }
          }
        )
    }

}
