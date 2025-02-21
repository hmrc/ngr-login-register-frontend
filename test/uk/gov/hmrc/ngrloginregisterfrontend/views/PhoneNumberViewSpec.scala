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

package uk.gov.hmrc.ngrloginregisterfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.PhoneNumber
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.PhoneNumberView

class PhoneNumberViewSpec extends ViewBaseSpec {

  lazy val phoneNumberView: PhoneNumberView = inject[PhoneNumberView]
  lazy val pageTitle = "Phone Number"
  lazy val backLink = "Back"
  lazy val content = "Register for the business rates valuation service"
  lazy val heading = "Enter phone number"
  lazy val label = "Phone Number"
  lazy val hint = "For international numbers include the country code"
  lazy val continueButton = "Continue"
  lazy val emptyErrorMessage = "error.browser.title.prefixEnter your Phone number"
  lazy val invalidErrorMessage = "error.browser.title.prefixPlease enter a valid phone number"

  val caption = "Register for the business rates valuation service"

  object Selectors {
    val caption = "#content > form > span"
    val heading = "#content > form > h1"
    val label   = "#content > form > div > label"
    val hint   = "#hint-phoneNumber_value"
    val continueButton   = "#continue"
    val backLink = "#content > a"
    val errorMessage = "#error-message-phoneNumber_value-input"
  }

  "PhoneNumberView" must {
    "produce the same output for apply() and render()" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07954009726"))
      val htmlApply = phoneNumberView.apply(form).body
      val htmlRender = phoneNumberView.render(form, request, messages, mockConfig).body
      val htmlF = phoneNumberView.f(form)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint) mustBe hint
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing number error correctly " in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber(""))
      val htmlApply = phoneNumberView.apply(form).body
      val htmlRender = phoneNumberView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint) mustBe hint
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly " in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("invalid"))
      val htmlApply = phoneNumberView.apply(form).body
      val htmlRender = phoneNumberView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint) mustBe hint
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
