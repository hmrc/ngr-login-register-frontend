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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Session}
import uk.gov.hmrc.http.BadRequestException
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup._
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookUpAddresses
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.repo.NgrFindAddressRepo
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FindAddressController @Inject()(findAddressView: FindAddressView,
                                      addressLookupConnector: AddressLookupConnector,
                                      sessionManager: SessionManager,
                                      authenticate: AuthJourney,
                                      ngrFindAddressRepo: NgrFindAddressRepo,
                                      mcc: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def show(mode: String): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      Future.successful(Ok(findAddressView(form(), mode)))
    }
  }

  def submit(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      FindAddress.form()
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(findAddressView(formWithErrors, mode))),
          findAddress => {
            addressLookupConnector.findAddressByPostcode(findAddress.postcode.value, findAddress.propertyName) map {
              case AddressLookupErrorResponse(e: BadRequestException) =>
                BadRequest(e.message)
              case AddressLookupErrorResponse(_) =>
                InternalServerError
              case AddressLookupSuccessResponse(recordSet) =>
                val addressLookupResponseSession = sessionManager.setAddressLookupResponse(request.session, recordSet.candidateAddresses.map(address => address.address))
                val addressAndPostcodeSession: Session = sessionManager.setPostcode(addressLookupResponseSession, Postcode(findAddress.postcode.value))
                ngrFindAddressRepo.upsert(LookUpAddresses(credId = CredId(request.credId.getOrElse("")), addressList = recordSet.candidateAddresses.map(address => address.address)))
                Redirect(routes.AddressSearchResultController.show(page = 1, mode)).withSession(addressAndPostcodeSession)
            }
          })
    }
}



