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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.VoaSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.AgentStatus.Autonomous
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, ContactNumber, Email, Link, Postcode, RatepayerRegistration, VoaSummaryListRow, Name}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.CheckAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CheckAnswersController @Inject()(view: CheckAnswersView,
                                       authenticate: AuthJourney,
                                       mcc: MessagesControllerComponents)(implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  private val testUser = RatepayerRegistration(Individual, Autonomous, Name("Phil Jones"), None, Email("this@that.com"), ContactNumber("07874346758"), None, Address("12 Nice Lane", None, "Goodtown", None, Postcode("M19 3FW"), "UK"))

  private def createRowsFromUserData(userData: RatepayerRegistration): Seq[VoaSummaryListRow] = {
    Seq(
      VoaSummaryListRow("Name", Seq(userData.name.value), Some(Link(Call("GET", "url"), "linkid", "Change"))),
      VoaSummaryListRow("Email", Seq(userData.email.value), Some(Link(Call("GET", "url"), "linkid", "Change"))),
      VoaSummaryListRow("Contact number", Seq.empty, Some(Link(Call("GET", "url"), "linkid", "Add"))),
      VoaSummaryListRow("Address", Seq(userData.address.line1, userData.address.line2.getOrElse(""),userData.address.town,userData.address.postcode.value,userData.address.country).filter(_.nonEmpty), Some(Link(Call("GET", "url"), "linkid", "Change")))
    )
  }

  private def summaryListRows()(implicit messages: Messages): Seq[SummaryListRow] = createRowsFromUserData(testUser).map(summarise)

  def show: Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>
      Future.successful(Ok(view(SummaryList(summaryListRows()))))
    }
  }

}
