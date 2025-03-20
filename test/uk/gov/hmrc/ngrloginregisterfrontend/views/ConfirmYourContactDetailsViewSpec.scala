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
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView

class ConfirmYourContactDetailsViewSpec extends ViewBaseSpec {

  val confirmContactDetailsView: ConfirmContactDetailsView = app.injector.instanceOf[ConfirmContactDetailsView]
  val pageTitle = "Confirm your contact details - GOV.UK"
  val pageHeading = "Confirm your contact details"
  val backText = "Back"
  val contactNameTitle = "Contact name"
  val emailTitle = "Email address"
  val phoneNumberTitle = "Phone number"
  val addressTitle = "Address"
  val contactNameValue = "John Joe Ferguson"
  val changeLinkText = "Change"
  val addLinkText = "Add"
  val emailValue = "test@test.co.uk"
  val addressValue = "26 FARADAY DRIVE PO BOX 45 LONDON CT1 1RQ GREAT BRITAIN"
  val continueText = "Continue"

  object Selectors {
    val heading = "#main-content > div > div > h1"
    val backLink = ".govuk-back-link"
    val title: Int => String = selectorNumber => s"#main-content > div > div > dl > div:nth-child($selectorNumber) > dt"
    val value: Int => String = selectorNumber => s"#main-content > div > div > dl > div:nth-child($selectorNumber) > dd.govuk-summary-list__value"
    val contactNameChangeLink = "#changeName"
    val emailChangeLink = "#changeEmail"
    val phoneNumberAddLink = "#addPhoneNumber"
    val addressChangeLink = "#changeAddress"
    val continueButton = "#continue"
  }

  "Rendering the ConfirmContactDetailsView" should {
    lazy val view = confirmContactDetailsView(SummaryList(createSummaryListRows()), "name")
    lazy implicit val document: Document = Jsoup.parse(view.body)
    lazy val htmlF = confirmContactDetailsView.f(SummaryList(createSummaryListRows()), "name")(request, messages, mockConfig)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }
    "have the correct document title" in {
      document.title mustBe pageTitle
    }
    "have the correct heading" in {
      elementText(Selectors.heading) mustBe pageHeading
    }
    "A Summary List component is rendered" which {
      "should render a contactName row with a title, value and change link" in {
        elementText(Selectors.title(1)) mustBe contactNameTitle
        elementText(Selectors.value(1)) mustBe contactNameValue
        elementText(Selectors.contactNameChangeLink) mustBe changeLinkText
      }
      "should render an Email row with a title, value and change link" in {
        elementText(Selectors.title(2)) mustBe emailTitle
        elementText(Selectors.value(2)) mustBe emailValue
        elementText(Selectors.emailChangeLink) mustBe changeLinkText
      }
      "should render an Phone number row with a title and add link as no value has been provided" in {
        elementText(Selectors.title(3)) mustBe phoneNumberTitle
        elementText(Selectors.phoneNumberAddLink) mustBe addLinkText
      }
      "should render an Address row with title, value and change link" in {
        elementText(Selectors.title(4)) mustBe addressTitle
        elementText(Selectors.value(4)) mustBe addressValue
        elementText(Selectors.addressChangeLink) mustBe changeLinkText
      }
    }
    "have a continue button" in {
      elementText(Selectors.continueButton) mustBe continueText
    }
    }
}
