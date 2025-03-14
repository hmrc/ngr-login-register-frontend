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
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.{ErrorResponse, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{Address, AddressLookupRequest, AddressLookupResponse, Subdivision}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress.form
import uk.gov.hmrc.ngrloginregisterfrontend.session.SessionManager
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

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

  // $COVERAGE-OFF$
  val testAddressModel: Int => Address = number =>
    Address(
      lines = Seq(s"$number Wibble Rd", "Berry Head Road"),
      town = "Worthing",
      postcode = "HA49EY",
      subdivision = Some(Subdivision(
        code = "code",
        name = "name"
      )),
      country = Subdivision(
        code = "GB",
        name = "Great Britain"
      )
    )
  // $COVERAGE-ON$

  lazy val testAddressList: Seq[Address] = for (i <- 1 to 7) yield testAddressModel(i)

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
              // $COVERAGE-OFF$
              case "false" =>
                val addresses: Seq[Address] = testAddressList
                val addressLookupResponseSession: Session = sessionManager.setAddressLookupResponse(request.session, addresses)
                val addressAndPostcodeSession: Session = sessionManager.setPostcode(addressLookupResponseSession, Postcode(findAddress.postcode.value))
                Future.successful(Redirect(routes.AddressSearchResultController.show(page = 1)).withSession(addressAndPostcodeSession))
              // $COVERAGE-ON$
              case _ =>
                addressLookupConnector.findAddressByPostcode(AddressLookupRequest(findAddress.postcode.value, findAddress.propertyName))
                  .flatMap {
                    case Right(responses: Seq[AddressLookupResponse]) =>
                      val addresses: Seq[Address] = responses.map(_.address)
                      val session: Session = request.session
                      sessionManager.removeSessionKey(session, sessionManager.addressLookupResponseKey)
                      sessionManager.removeSessionKey(session, sessionManager.postcodeKey)
                      val addressLookupResponseSession = sessionManager.setAddressLookupResponse(request.session, addresses)
                      val addressAndPostcodeSession: Session = sessionManager.setPostcode(addressLookupResponseSession, Postcode(findAddress.postcode.value))
                      Future.successful(Redirect(routes.AddressSearchResultController.show(page = 1)).withSession(addressAndPostcodeSession))
                    case Left(errorResponse: ErrorResponse) =>
                      logger.error(s"AddressLookup has returned an error: status ${errorResponse.code}, ${errorResponse.message}")
                      Future.successful(InternalServerError(Json.toJson(errorResponse)))
                  }
            }
          }
        )
    }

}
