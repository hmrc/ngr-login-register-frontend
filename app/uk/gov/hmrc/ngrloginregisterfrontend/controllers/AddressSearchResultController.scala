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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result, Session}
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookedUpAddress
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SessionTimeoutHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AddressSearchResultController @Inject()(view:  AddressSearchResultView,
                                              authenticate: AuthJourney,
                                              mcc: MessagesControllerComponents,
                                              sessionManager: SessionManager
                                             )(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with SessionTimeoutHelper {

  private lazy val defaultPageSize: Int = 15

  def show(page: Int = 1, mode: String): Action[AnyContent] = {
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

      def zipWithIndex(currentPage: Int,pageSize: Int, address: Seq[String]): Seq[(String, String)] = {
        val url = (i: Int) => if (page > 1) {
          routes.AddressSearchResultController.selectedAddress(i + defaultPageSize, mode).url
        } else {
          routes.AddressSearchResultController.selectedAddress(i, mode).url
        }
        splitAddressByPage(currentPage, pageSize, address).zipWithIndex.map(x => (x._1, url(x._2)))
      }

      def generateTable(addressList:AddressSearchResult): Table  = {
        TableData(
          headers = Seq(TableHeader("Address", "govuk-table__caption--m", colspan = Some(2))),
          rows = zipWithIndex(page, defaultPageSize, addressList.address)
            .map(stringValue => Seq(TableRowText(stringValue._1), TableRowLink(stringValue._2, "Select Property")))
        ).toTable
      }

       def pageBottom: Int = PaginationData.pageBottom(currentPage = page, pageSize = defaultPageSize)
       def pageTop: Int = PaginationData.pageTop(currentPage = page, pageSize = defaultPageSize, address.length)

      Future.successful(Ok(view(
        postcode = postcode,
        paginationData = PaginationData(totalPages = totalPages, currentPage = page, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = defaultPageSize),
        totalAddress = address.length,
        pageTop = pageTop,
        pageBottom = pageBottom + (if (pageTop == 0) 0 else 1),
        addressSearchResultTable = generateTable(AddressSearchResult(address)),
        mode = mode
      )))
    }
  }

  def selectedAddress(index: Int, mode: String): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      getSession(sessionManager, request.session, sessionManager.addressLookupResponseKey) match {
        case Right(addressListOpt) => setChosenAddress(addressListOpt, index, request.session, mode)
        case Left(result) => Future.successful(result)
      }
    }
  }

  private def setChosenAddress(addressListOpt: Option[String], index: Int, session: Session, mode: String): Future[Result] = {
    getSelectedAddress(addressListOpt, index)
      .map(address =>
        Future.successful(
          Redirect(routes.ConfirmAddressController.show(mode)).withSession(sessionManager.setChosenAddress(session, address))
        )
      )
      .getOrElse(Future.failed(new RuntimeException("Address not found at index")))
  }

  private def getSelectedAddress(addressListOpt: Option[String], index: Int): Option[LookedUpAddress] = {
    addressListOpt
      .map(Json.parse(_).as[Seq[LookedUpAddress]])
      .flatMap(addresses =>
        Try(addresses.apply(index)) match {
          case Failure(e) => None
          case Success(address) => Some(address)
        }
      )
  }
}
