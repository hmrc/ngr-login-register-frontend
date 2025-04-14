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
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ManualAddressView

class ManualAddressViewSpec extends ViewBaseSpec {

  lazy val over130Characters = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"

  lazy val findAddressView: ManualAddressView = inject[ManualAddressView]
  lazy val backLink = "Back"
  lazy val content = "Register for the business rates valuation service"
  lazy val heading = "What is the address?"
  lazy val addressLine1Label = "Address line 1 (optional)"
  lazy val addressLine2Label = "Address line 2 (optional)"
  lazy val townOrCityLabel  = "Town or city (optional)"
  lazy val countyLabel      = "County (optional)"
  lazy val postcodeLabel    = "Postcode"
  lazy val postcodeFormErrorNotSupplied  = "Enter postcode"
  lazy val postcodeErrorNotSupplied  = "Error: Enter postcode"
  lazy val postcodeFormErrorInvalid  = "Enter a full UK postcode"
  lazy val postcodeErrorInvalid  = "Error: Enter a full UK postcode"

  val caption = "Register for the business rates valuation service"

  object Selectors {
    val heading = "#main-content > div > div > form > h1"
    val addressLine1Label  = "#main-content > div > div > form > div:nth-child(2) > label"
    val addressLine2Label  = "#main-content > div > div > form > div:nth-child(3) > label"
    val townOrCityLabel    = "#main-content > div > div > form > div:nth-child(4) > label"
    val countyLabel        = "#main-content > div > div > form > div:nth-child(5) > label"
    val postcodeLabel      = "#main-content > div > div > form > div:nth-child(6) > label"
    val backLink = "body > div > a"
    val postcodeErrorMessage = "#PostalCode-error"
    val addressLine1ErrorMessage = "#AddressLine1-error"
    val townErrorMessage = "#City-error"
    val formErrorMessage = "#main-content > div > div > form > div.govuk-error-summary > div > div > ul > li > a"
  }

  "FindAddressView" must {

    "print valid description" in {
      val findAddress = Address(line1 = "99", line2 = Some("Wibble Rd"), town = "Worthing", county = Some("West Sussex"), postcode = Postcode("TQ5 9BW"))
      findAddress.toString mustBe "99, Wibble Rd, Worthing, West Sussex, TQ5 9BW"
    }


    "produce the same output for apply() and render()" in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "99", line2 = Some("Wibble Rd"), town = "Worthing", county = Some("West Sussex"), postcode = Postcode("TQ5 9BW")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.addressLine1Label) mustBe addressLine1Label
      elementText(Selectors.addressLine2Label) mustBe addressLine2Label
      elementText(Selectors.townOrCityLabel) mustBe townOrCityLabel
      elementText(Selectors.countyLabel) mustBe countyLabel
      elementText(Selectors.postcodeLabel) mustBe postcodeLabel
    }

    "produce the same output for apply() and render() when valid postcode without space in between" in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "99", line2 = Some("Wibble Rd"), town = "Worthing", county = Some("West Sussex"), postcode = Postcode("TQ59BW")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.addressLine1Label) mustBe addressLine1Label
      elementText(Selectors.addressLine2Label) mustBe addressLine2Label
      elementText(Selectors.townOrCityLabel) mustBe townOrCityLabel
      elementText(Selectors.countyLabel) mustBe countyLabel
      elementText(Selectors.postcodeLabel) mustBe postcodeLabel
    }

    "produce the same output for apply() and render() with no county and address line two" in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "99", line2 = None, town = "Worthing", county = None, postcode = Postcode("TQ59BW")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.addressLine1Label) mustBe addressLine1Label
      elementText(Selectors.addressLine2Label) mustBe addressLine2Label
      elementText(Selectors.townOrCityLabel) mustBe townOrCityLabel
      elementText(Selectors.countyLabel) mustBe countyLabel
      elementText(Selectors.postcodeLabel) mustBe postcodeLabel
    }

    "produce the same output for apply() and render() with only postcode" in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "", line2 = None, town = "", county = None, postcode = Postcode("TQ59BW")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      val htmlF = findAddressView.f(form, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.addressLine1Label) mustBe addressLine1Label
      elementText(Selectors.addressLine2Label) mustBe addressLine2Label
      elementText(Selectors.townOrCityLabel) mustBe townOrCityLabel
      elementText(Selectors.countyLabel) mustBe countyLabel
      elementText(Selectors.postcodeLabel) mustBe postcodeLabel
    }

    "show missing postcode error correctly " in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "99", line2 = None, town = "Worthing", county = None, postcode = Postcode("")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.formErrorMessage) mustBe postcodeFormErrorNotSupplied
      elementText(Selectors.postcodeErrorMessage) mustBe postcodeErrorNotSupplied
    }

    "show invalid postcode format error correctly " in {
      val form = Address
        .form()
        .fillAndValidate(Address(line1 = "99", line2 = None, town = "Worthing", county = None, postcode = Postcode("AAA9 9AA")))
      val htmlApply = findAddressView.apply(form, confirmContactDetailsMode).body
      val htmlRender = findAddressView.render(form, confirmContactDetailsMode, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(findAddressView(form, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.formErrorMessage) mustBe postcodeFormErrorInvalid
      elementText(Selectors.postcodeErrorMessage) mustBe postcodeErrorInvalid
    }
  }
}
