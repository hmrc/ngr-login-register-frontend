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
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.PaginatedAddress
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.Address
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

  lazy val defaulPageSize = 5

  def show(page: Int = 1): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
     val address: Seq[String] =  sessionManager.getSessionValue(request.session, sessionManager.addressLookupResponseKey).map {
        sessionData =>
          Json.parse(sessionData).as[Seq[Address]].map(address => s"${address.lines.mkString(",")} ${address.town}, ${address.postcode}")
      }.getOrElse(Seq.empty)
      val postcode: String = sessionManager.getSessionValue(request.session, sessionManager.postcodeKey).getOrElse("")

      val mockPaginatedAddress = PaginatedAddress(
        currentPage = page,
        total = address.length,
        pageSize = defaulPageSize,
        address = PaginatedAddress.pageAddress(currentPage = page, pageSize = defaulPageSize, address = address),
        links = PaginatedAddress.displayPaginateLinks(currentPage = page, total = address.length, pageSize = defaulPageSize)
      )

      Future.successful(Ok(view(
        postcode = postcode,
        paginatedData = Some(mockPaginatedAddress),
        totalAddress = address.length,
        pageTop = PaginatedAddress.pageTop(currentPage = page, pageSize = defaulPageSize, address.length),
        pageBottom = PaginatedAddress.pageBottom(currentPage = page, pageSize = defaulPageSize) + 1
      )))
    }
  }

  def selectedAddress(index: Int): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      sessionManager.getSessionValue(request.session, sessionManager.addressLookupResponseKey).map{
        sessionData => Json.parse(sessionData).as[Seq[Address]]
      }.getOrElse(Seq.empty) match {
        case address if address.nonEmpty =>
          val updateSession: Session = sessionManager.setChosenAddress(request.session, address.apply(index).toString)
          Future.successful(Redirect(routes.NameController.show).withSession(updateSession))
        case _ =>
          Future.failed(new RuntimeException("Address not found at index"))
      }
    }
  }
}
