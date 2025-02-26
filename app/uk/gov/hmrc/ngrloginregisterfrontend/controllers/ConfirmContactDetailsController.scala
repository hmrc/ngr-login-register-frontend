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

import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthJourney,
                                                mcc: MessagesControllerComponents,
                                                citizenDetailsConnector: CitizenDetailsConnector)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      citizenDetailsConnector.getPersonDetails(Nino(request.nino.nino.getOrElse(""))).map {
        case Left(error) => Status(error.code)(Json.toJson(error))
        case Right(personDetails) => Ok(view(SummaryList(createSummaryRows(personDetails, request)), name(personDetails)))
      }
    }

  def name(personDetails: PersonDetails): String = List(
    personDetails.person.firstName,
    personDetails.person.middleName,
    personDetails.person.lastName
  ).flatten.mkString(" ")

  private[controllers] def createSummaryRows(personDetails: PersonDetails, request: AuthenticatedUserRequest[AnyContent])(implicit messages: Messages): Seq[SummaryListRow] = {

    val address: Seq[String] = Seq(
      personDetails.address.line1.getOrElse(""),
      personDetails.address.line2.getOrElse(""),
      personDetails.address.line3.getOrElse(""),
      personDetails.address.line4.getOrElse(""),
      personDetails.address.line5.getOrElse(""),
      personDetails.address.postcode.getOrElse(""),
      personDetails.address.country.getOrElse("")
    ).filter(_.nonEmpty)

    Seq(
      NGRSummaryListRow(messages("confirmContactDetails.contactName"), None, Seq(name(personDetails)), Some(Link(Call("GET", routes.NameController.show.url), "name-linkid", "Change"))),
      NGRSummaryListRow(messages("confirmContactDetails.emailAddress"), None, Seq(request.email.getOrElse("")), Some(Link(Call("GET", "url"), "email-linkid", "Change"))),
      NGRSummaryListRow(messages("confirmContactDetails.phoneNumber"), None, Seq.empty, Some(Link(Call("GET", routes.PhoneNumberController.show.url), "number-linkid", "Add"))),
      NGRSummaryListRow(messages("confirmContactDetails.address"), Some(messages("confirmContactDetails.address.caption")), address, Some(Link(Call("GET", "url"), "address-linkid", "Change")))
    ).map(summarise)
  }

}
