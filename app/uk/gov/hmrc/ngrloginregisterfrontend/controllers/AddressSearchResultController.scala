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
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, PaginatedAddress, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressSearchResultController @Inject()(view:  AddressSearchResultView,
                                              authenticate: AuthJourney,
                                              mcc: MessagesControllerComponents,
                                              sessionManager: SessionManager
                                             )(implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  lazy val defaulPageSize = 5

  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel2: Address =
    Address(line1 = "100",
      line2 = Some("Croft Rd"),
      town = "Uxbridge",
      county = Some("Hillingdon"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel3: Address =
    Address(line1 = "20",
      line2 = Some("Long Rd"),
      town = "Bournemouth",
      county = Some("Dorset"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  lazy val testAddressList = Seq(testAddressModel3, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel)
  lazy val testPostcode = testAddressModel.postcode.value

  def show(page: Int = 1): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>

      //TODO anna connector is needed to populate address's


      val mockPaginatedAddress = PaginatedAddress(
        currentPage = page,
        total = testAddressList.length,
        pageSize = defaulPageSize,
        address = PaginatedAddress.pageAddress(currentPage = page, pageSize = defaulPageSize, address = testAddressList),
        links = PaginatedAddress.displayPaginateLinks(currentPage = page, total = testAddressList.length, pageSize = defaulPageSize)
      )

      Future.successful(Ok(view(
        postcode = testPostcode,
        paginatedData = Some(mockPaginatedAddress),
        totalAddress = testAddressList.length,
        pageTop = PaginatedAddress.pageTop(currentPage = page, pageSize = defaulPageSize, testAddressList.length),
        pageBottom = PaginatedAddress.pageBottom(currentPage = page, pageSize = defaulPageSize) + 1
      )))
    }
  }

  def selectedAddress(index: Int): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      //val getAddressSession: Seq[Address] = sessionManager.getAddressLookupResponse() //TODO anna's session function to grab all address's
      val updateSession: Session = sessionManager.setChosenAddress(request.session, testAddressList.apply(index).toString)
      println(s"session ${request.session}")
      println(s"address ${testAddressList.apply(index).toString}")
      Future.successful(Redirect(routes.NameController.show).withSession(updateSession))
    }
  }
}
