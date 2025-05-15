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
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.ngrloginregisterfrontend.repo.NgrFindAddressRepo
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressSearchResultController @Inject()(view:  AddressSearchResultView,
                                              authenticate: AuthRetrievals,
                                              isRegisteredCheck: RegistrationAction,
                                              hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                              mcc: MessagesControllerComponents,
                                              ngrFindAddressRepo: NgrFindAddressRepo
                                             )(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val defaultPageSize: Int = 15

  def show(page: Int = 1, mode: String): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async { implicit request =>
      ngrFindAddressRepo.findByCredId(CredId(request.credId.value)).flatMap {
        case None =>
          Future.successful(Redirect(routes.FindAddressController.show(mode)))
        case Some(addresses) =>
          val address:Seq[String] = addresses.addressList.map(_.toString)
          val postcode: String = addresses.postcode.value
          Future.successful(createPaginateView(address, postcode, page, mode))
      }
    }
  }

  def selectedAddress(index: Int, mode: String): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction)async { _ =>
      Future.successful(Redirect(routes.ConfirmAddressController.show(mode, index)))
    }
  }

  private def createPaginateView(address: Seq[String], postcode: String, page: Int, mode: String)(implicit request: RequestHeader): Result = {
    def totalPages: Int = math.ceil(address.length.toFloat / defaultPageSize.toFloat).toInt

    def splitAddressByPage(currentPage: Int, pageSize: Int, address: Seq[String]): Seq[String] = {
      PaginationData.getPage(currentPage = currentPage, pageSize = pageSize, list = address)
    }

    def zipWithIndex(currentPage: Int, pageSize: Int, address: Seq[String]): Seq[(String, String)] = {
      val url = (i: Int) => if (page > 1) {
        routes.AddressSearchResultController.selectedAddress(i + defaultPageSize, mode).url
      } else {
        routes.AddressSearchResultController.selectedAddress(i, mode).url
      }
      splitAddressByPage(currentPage, pageSize, address).zipWithIndex.map(x => (x._1, url(x._2)))
    }

    def generateTable(addressList: AddressSearchResult): Table = {
      TableData(
        headers = Seq(TableHeader("Address", "govuk-table__caption--m", colspan = Some(2))),
        rows = zipWithIndex(page, defaultPageSize, addressList.address)
          .map(stringValue => Seq(TableRowText(stringValue._1), TableRowLink(stringValue._2, "Select Property")))
      ).toTable
    }

    def pageBottom: Int = PaginationData.pageBottom(currentPage = page, pageSize = defaultPageSize)

    def pageTop: Int = PaginationData.pageTop(currentPage = page, pageSize = defaultPageSize, address.length)

    Ok(view(
      postcode = postcode,
      paginationData = PaginationData(totalPages = totalPages, currentPage = page, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = defaultPageSize),
      totalAddress = address.length,
      pageTop = pageTop,
      pageBottom = pageBottom + (if (pageTop == 0) 0 else 1),
      addressSearchResultTable = generateTable(AddressSearchResult(address)),
      mode = mode
    ))
  }
}
