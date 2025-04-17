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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.BadRequestException
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup.{AddressLookupConnector, AddressLookupErrorResponse, AddressLookupSuccessResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookUpAddresses
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address.form
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.repo.NgrFindAddressRepo
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ManualAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ManualAddressController @Inject()(addressView: ManualAddressView,
                                        connector: NGRConnector,
                                        addressLookupConnector: AddressLookupConnector,
                                        ngrFindAddressRepo: NgrFindAddressRepo,
                                        authenticate: AuthJourney,
                                        mcc: MessagesControllerComponents,
                                       )(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show(mode: String): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      connector.getRatepayer(CredId(request.credId.getOrElse(""))).map { ratepayerOpt =>
        val addressForm = ratepayerOpt
          .flatMap(_.ratepayerRegistration)
          .flatMap(_.address)
          .map(address => form().fill(Address(
            line1 = address.line1,
            line2 = address.line2,
            town = address.town,
            county = None,
            postcode = address.postcode)))
          .getOrElse(form())
        Ok(addressView(addressForm, mode))
      }
    }
  }

  def submit(mode: String): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      Address.form()
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(addressView(formWithErrors, mode))),
          findAddress => {
            addressLookupConnector.findAddressByPostcode(findAddress.postcode.value, setFilter(findAddress)) map {
              case AddressLookupErrorResponse(e: BadRequestException) =>
                BadRequest(e.message)
              case AddressLookupErrorResponse(_) =>
                InternalServerError
              case AddressLookupSuccessResponse(recordSet) =>
                ngrFindAddressRepo.upsertLookupAddresses(LookUpAddresses(credId = CredId(request.credId.getOrElse("")), postcode = findAddress.postcode, addressList = recordSet.candidateAddresses.map(address => address.address)))
                Redirect(routes.AddressSearchResultController.show(page = 1, mode))
            }
          })
    }

  def setFilter(findAddress: Address): Option[String] = {
    val filterString = Seq(findAddress.line1,
      findAddress.line2.getOrElse(""),
      findAddress.town,
      findAddress.county.getOrElse("")
    ).filter(_.nonEmpty)
      .mkString(" ")

    if (filterString.nonEmpty)
      Some(filterString)
    else
      None
  }
}
