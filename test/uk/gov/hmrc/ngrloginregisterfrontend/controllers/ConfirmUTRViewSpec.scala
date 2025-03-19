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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.SaUtr
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmUTR
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmUTRView

class ConfirmUTRViewSpec extends ViewBaseSpec {
  lazy val view: ConfirmUTRView = inject[ConfirmUTRView]
  lazy val controller: ConfirmUTRController = inject[ConfirmUTRController]

  val hint: String = "Register for the business rates valuation service"
  val title: String = "Confirm your Self Assessment Unique Taxpayer Reference"
  val body: String = "We will display the last 3 digits of your Unique Taxpayer Reference (UTR). You can provide this UTR to join up the accounts you use to pay tax."
  val sautr: String = "Self Assessment Unique Taxpayer Reference"
  val yes: String = "Yes, I want to provide this UTR"
  val noNI: String = "No, I want to provide my National Insurance number"
  val noLater: String = "No, I will provide a tax reference number later"
  val errorMessage: String = "Please select an option"

  object Selectors {
    val hint: String = "#main-content > div > div > form > span"
    val title: String = "#main-content > div > div > form > h1"
    val body: String = "#main-content > div > div > form > p"
    val sautr: String = "#main-content > div > div > form > dl > div > dt"
    val yes: String = "#main-content > div > div > form > div > div > div:nth-child(1) > label"
    val noNI: String = "#main-content > div > div > form > div > div > div:nth-child(2) > label"
    val noLater: String = "#main-content > div > div > form > div > div > div:nth-child(3) > label"
    val errorMessage: String = "#main-content > div > div > form > div.govuk-error-summary > div > div > ul > li > a"
  }

  "ConfirmUTRView" must {
    "produce the same output for apply() and render()" in {
      val form = ConfirmUTR
        .form()

      val utr: SaUtr = SaUtr("1234567890")

      val summaryList = controller.summaryList(utr.value)

      val radios = controller.radios()

      val htmlApply = view.apply(form, summaryList, radios).body

      val htmlRender = view.render(form, summaryList, radios, request, messages, mockConfig).body

      val htmlF = view.f(form, summaryList, radios)(request, messages, mockConfig).body

      htmlApply mustBe htmlRender
      htmlF must not be empty
      lazy implicit val document: Document = Jsoup.parse(view(form, summaryList, radios)(request, messages, mockConfig).body)
      elementText(Selectors.hint) mustBe hint
      elementText(Selectors.title) mustBe title
      elementText(Selectors.body) mustBe body
      elementText(Selectors.sautr) mustBe sautr
      elementText(Selectors.yes) mustBe yes
      elementText(Selectors.noNI) mustBe noNI
      elementText(Selectors.noLater) mustBe noLater
    }
  }

}
