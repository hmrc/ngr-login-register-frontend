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
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddress
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.Future

class AddressSearchResultController @Inject()(view:  AddressSearchResultView,
                                              authenticate: AuthJourney,
                                              mcc: MessagesControllerComponents,
                                              sessionManager: SessionManager
                                             )(implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  private lazy val defaultPageSize: Int = 15

  def show(page: Int = 1): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
     val address: Seq[String] =  sessionManager.getSessionValue(request.session, sessionManager.addressLookupResponseKey)
       .map(
         Json.parse(_).as[Seq[LookedUpAddress]]
         .map(address => s"${address.lines.mkString(", ")}, ${address.town}, ${address.postcode}")
       )
       .getOrElse(Seq.empty)

      val postcode: String = sessionManager.getSessionValue(request.session, sessionManager.postcodeKey).getOrElse("")
      val totalPages: Int = math.ceil(address.length.toFloat / defaultPageSize.toFloat).toInt
      def splitAddressByPage(currentPage: Int,pageSize: Int, address: Seq[String]): Seq[String] = {
        PaginationData.getPage(currentPage = currentPage, pageSize = pageSize, list = address)
      }

      def zipWithIndex(currentPage: Int,pageSize: Int, address: Seq[String]): Seq[(String, String)] =
        splitAddressByPage(currentPage, pageSize, address).zipWithIndex.map(
        x => (x._1, if(page > 1){routes.AddressSearchResultController.selectedAddress(x._2 + defaultPageSize).url} else {routes.AddressSearchResultController.selectedAddress(x._2).url})
      )

      def generateTable(addressList:AddressSearchResult): Table  = {
        TableData(headers = Seq(TableHeader("Address", "govuk-table__caption--m", colspan = Some(2))), rows = zipWithIndex(page, defaultPageSize, addressList.address).map(stringValue => Seq(TableRowText(stringValue._1), TableRowLink(stringValue._2, "Select Property")))).toTable
      }

      Future.successful(Ok(view(
        postcode = postcode,
        paginationData = PaginationData(totalPages = totalPages, currentPage = page, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = defaultPageSize),
        totalAddress = address.length,
        pageTop = PaginationData.pageTop(currentPage = page, pageSize = defaultPageSize, address.length),
        pageBottom = PaginationData.pageBottom(currentPage = page, pageSize = defaultPageSize) + 1,
        addressSearchResultTable = generateTable(AddressSearchResult(address)
       )
      )))
    }
  }

  def selectedAddress(index: Int): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      sessionManager.getSessionValue(request.session, sessionManager.addressLookupResponseKey)
        .map(Json.parse(_).as[Seq[LookedUpAddress]])
        .map(_.apply(index))
        .map(address =>
          Future.successful(
            Redirect(routes.ConfirmAddressController.show).withSession(sessionManager.setChosenAddress(request.session, address))
          )
        )
        .getOrElse(Future.failed(new RuntimeException("Address not found at index")))
    }
  }
}
