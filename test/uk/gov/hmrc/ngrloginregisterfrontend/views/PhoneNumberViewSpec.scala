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
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.PhoneNumber
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.PhoneNumberView

class PhoneNumberViewSpec extends ViewBaseSpec {

  lazy val phoneNumberView: PhoneNumberView = inject[PhoneNumberView]
  lazy val pageTitle = "Phone Number"
  lazy val backLink = "Back"
  lazy val content = "Register for the business rates valuation service"
  lazy val heading = "Enter phone number"
  lazy val label = "UK phone number"
  lazy val continueButton = "Continue"
  lazy val emptyErrorMessage = "Error: Enter your phone number"
  lazy val invalidErrorMessage = "Error: Please enter a valid phone number"

  val caption = "Register for the business rates valuation service"

  object Selectors {
    val caption = "#main-content > div > div > form > span"
    val heading = "#main-content > div > div > form > h1"
    val label   = "#main-content > div > div > form > div > label"
    val hint   = "#hint-phoneNumber_value"
    val continueButton   = "#continue"
    val backLink = "body > div > a"
    val errorMessage = "#phoneNumber-value-error"
  }

  "PhoneNumberView" must {
    "produce the same output for apply() and render()" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07954009726"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      val htmlF = phoneNumberView.f(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "produce the same output for apply() and render() when no number in mongo" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07954009726"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      val htmlF = phoneNumberView.f(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show no error when a user inputs a valid number with spacing at the beginning" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("         07954009726"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, false).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, false, request, messages, mockConfig).body
      val htmlF = phoneNumberView.f(form, confirmContactDetailsMode, false)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, false)(request, messages, mockConfig).body)
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing number error correctly " in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber(""))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly " in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("invalid"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when (01) is used" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("(01)07943009506"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when number is too short" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07943009"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when + is used to valid number" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("(+07943009506"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when ! is used to valid number" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("(!07943009506"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when number is too long" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07943009506152692"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when () is used" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("()07943009506"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when () is used anywhere in the number" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07()943009506()"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid number format error correctly when - is used" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("079430-09506"))
      val htmlApply = phoneNumberView.apply(form, confirmContactDetailsMode, true).body
      val htmlRender = phoneNumberView.render(form, confirmContactDetailsMode, true, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(phoneNumberView(form, confirmContactDetailsMode, true)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
