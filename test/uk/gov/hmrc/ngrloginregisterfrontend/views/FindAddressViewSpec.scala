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
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.FindAddress
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FindAddressView

class FindAddressViewSpec extends ViewBaseSpec {

  lazy val findAddressView: FindAddressView = inject[FindAddressView]
  lazy val backLink = "Back"
  lazy val content = "Register for the business rates valuation service"
  lazy val heading = "Find the contact address"
  lazy val label = "Postcode"
  lazy val propertyNameLabel = "Property name or number (optional)"
  lazy val continueButton = "Find address"
  lazy val emptyErrorMessage = "Error: Enter a full UK postcode"
  lazy val invalidErrorMessage = "Error: Enter a full UK postcode"
  lazy val maxLengthErrorMessage = "Error: No more than 100 characters allowed"
  lazy val over100Characters = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"

  val caption = "Register for the business rates valuation service"

  object Selectors {
    val caption = "#content > form > span"
    val heading = "#content > form > h1"
    val label   = "#content > form > div > label"
    val hint   = "#hint-postcode-value"
    val continueButton   = "#continue"
    val backLink = "body > div > a"
    val errorMessage = "#postcode-value-error"
    val propertyNameErrorMessage = "#property-name-value-error"
  }

  "FindAddressView" must {

    "print valid description" in {
      val findAddress = FindAddress(Postcode("TQ5 9BW"), Some("20"))
      findAddress.toString mustBe "Some(20),TQ5 9BW"
    }

    "produce the same output for apply() and render()" in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode("TQ5 9BW"), None))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
//      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "produce the same output for apply() and render() when valid postcode without space in between" in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode("TQ59BW"), None))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
//      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "produce the same output for apply() and render() with valid property name" in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode("TQ5 9BW"), Some("5")))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
//      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing postcode error correctly " in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode(""), None))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
//      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid postcode format error correctly " in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode("AAA9 9AA"), None))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
//      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show property name exceed max length error correctly " in {
      val form = FindAddress
        .form()
        .fillAndValidate(FindAddress(Postcode("AA9 9AA"), Some(over100Characters)))
      val htmlApply = findAddressView.apply(form).body
      val htmlRender = findAddressView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.label)   mustBe label
      //      elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      elementText(Selectors.propertyNameErrorMessage) mustBe maxLengthErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
