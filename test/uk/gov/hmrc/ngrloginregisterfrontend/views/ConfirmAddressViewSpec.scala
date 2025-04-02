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
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrloginregisterfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName, No, Yes}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmAddressForm
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmAddressForm.form
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmAddressView

class ConfirmAddressViewSpec extends ViewBaseSpec {

  lazy val confirmAddressView: ConfirmAddressView = inject[ConfirmAddressView]
  lazy val backLink = "Back"
  lazy val title = "Do you want to use this address?"
  lazy val content = "Register for the business rates valuation service"
  lazy val heading = "Do you want to use this address?"
  lazy val yesLabel = "Yes"
  lazy val noLabel = "No"
  lazy val message = "We will send all letters to this address"
  lazy val continueButton = "Continue"
  lazy val unselectedRadioMessage = "This field is required"
  lazy val chosenAddress = "5 Wibble Rd, Worthing HA49EY"

  val caption = "Register for the business rates valuation service"

  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-address-radio"), Seq(yesButton, noButton))
  val radio: Radios = buildRadios(form, ngrRadio)

  object Selectors {
    val title = "head > title"
    val caption = "#main-content > div > div > form > span"
    val heading = "#main-content > div > div > form > h1"
    val yesLabel = "#main-content > div > div > form > div > div > div:nth-child(1) > label"
    val noLabel  = "#main-content > div > div > form > div > div > div:nth-child(2) > label"
    val chosenAddress  = "#main-content > div > div > form > p"
    val continueButton   = "#continue"
    val backLink = "body > div > a"
  }

  "ConfirmAddressView" must {

    "print valid description" in {
      val confirmAddress = ConfirmAddressForm("Yes")
      confirmAddress.toString mustBe "ConfirmAddressForm(Yes)"
    }

    "produce the same output for apply() and render() with yes radio selected" in {
      val form = ConfirmAddressForm.form.fillAndValidate(ConfirmAddressForm("Yes"))
      val htmlApply = confirmAddressView.apply(chosenAddress, form, radio, confirmContactDetailsMode).body
      val htmlRender = confirmAddressView.render(chosenAddress, form, radio, confirmContactDetailsMode, request, messages, mockConfig).body
      val htmlF = confirmAddressView.f(chosenAddress, form, radio, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      htmlApply.contains(message) shouldBe true
      lazy implicit val document: Document = Jsoup.parse(confirmAddressView(chosenAddress, form, radio, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.title) mustBe title
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.yesLabel)   mustBe yesLabel
      elementText(Selectors.noLabel)   mustBe noLabel
      elementText(Selectors.chosenAddress)   mustBe chosenAddress
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "produce the same output for apply() and render() with no radio selected" in {
      val form = ConfirmAddressForm.form.fillAndValidate(ConfirmAddressForm("No"))
      val htmlApply = confirmAddressView.apply(chosenAddress, form, radio, confirmContactDetailsMode).body
      val htmlRender = confirmAddressView.render(chosenAddress, form, radio, confirmContactDetailsMode,request, messages, mockConfig).body
      val htmlF = confirmAddressView.f(chosenAddress, form, radio, confirmContactDetailsMode)(request, messages, mockConfig).body
      htmlF must not be empty
      htmlApply mustBe htmlRender
      htmlApply.contains(message) shouldBe true
      lazy implicit val document: Document = Jsoup.parse(confirmAddressView(chosenAddress, form, radio, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.title) mustBe title
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.yesLabel)   mustBe yesLabel
      elementText(Selectors.noLabel)   mustBe noLabel
      elementText(Selectors.chosenAddress)   mustBe chosenAddress
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show unselected radio error correctly" in {
      val form = ConfirmAddressForm.form.fillAndValidate(ConfirmAddressForm(""))
      val htmlApply = confirmAddressView.apply(chosenAddress, form, radio, confirmContactDetailsMode).body
      val htmlRender = confirmAddressView.render(chosenAddress, form, radio, confirmContactDetailsMode, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      htmlApply.contains(unselectedRadioMessage) shouldBe true
      lazy implicit val document: Document = Jsoup.parse(confirmAddressView(chosenAddress, form, radio, confirmContactDetailsMode)(request, messages, mockConfig).body)
      elementText(Selectors.title) mustBe title
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.yesLabel)   mustBe yesLabel
      elementText(Selectors.noLabel)   mustBe noLabel
      elementText(Selectors.chosenAddress)   mustBe chosenAddress
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
