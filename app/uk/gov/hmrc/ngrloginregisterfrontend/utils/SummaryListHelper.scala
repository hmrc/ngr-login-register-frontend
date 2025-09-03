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

package uk.gov.hmrc.ngrloginregisterfrontend.utils

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Link, NGRSummaryListRow, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation

trait SummaryListHelper {
  def createContactDetailSummaryRows(ratepayerRegistrationValuation: RatepayerRegistrationValuation, mode: String)(implicit messages: Messages): SummaryList =
    createContactDetailSummaryRows(ratepayerRegistrationValuation, mode, "")

  def createContactDetailSummaryRows(ratepayerRegistrationValuation: RatepayerRegistrationValuation, mode: String, classes: String)(implicit messages: Messages): SummaryList = {

    def getValue[T](extract: RatepayerRegistration => Option[T]): Seq[String] =
      ratepayerRegistrationValuation.ratepayerRegistration
        .flatMap(extract)
        .map(value => Seq(value.toString))
        .getOrElse(Seq.empty)

    SummaryList(rows = deriveNGRSummaryRows(),
      classes = classes)
  }

  private def deriveNGRSummaryRows()(implicit messages: Messages): Seq[SummaryListRow] = {
    def getUrl(route: String, linkId: String, messageKey: String): Option[Link] =
      Some(Link(Call("GET", route), linkId, messageKey))

    Seq(
//      NGRSummaryListRow(messages("confirmContactDetails.contactName"), None, name, getUrl(routes.NameController.show(mode).url, "name-linkid", "Change")),
//      NGRSummaryListRow(messages("confirmContactDetails.emailAddress"), None, email, getUrl(routes.EmailController.show(mode).url, "email-linkid", "Change")),
//      NGRSummaryListRow(messages("confirmContactDetails.phoneNumber"), None, phone, getUrl(routes.PhoneNumberController.show(mode).url, "number-linkid",
//        if (phone.isEmpty) "confirmContactDetails.add" else "confirmContactDetails.change")),
//      NGRSummaryListRow(messages("confirmContactDetails.address"), Some(messages("confirmContactDetails.address.caption")), address, getUrl(routes.FindAddressController.show(mode).url, "address-linkid", "Change"))
    ).map(summarise)
  }
}
