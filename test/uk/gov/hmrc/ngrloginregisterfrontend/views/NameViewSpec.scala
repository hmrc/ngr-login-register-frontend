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
import uk.gov.hmrc.ngrloginregisterfrontend.models.Name
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NameView

class NameViewSpec  extends ViewBaseSpec {

  lazy val nameView: NameView = inject[NameView]
  lazy val backLink = "Back"
  lazy val caption = "Register for the business rates valuation service"
  lazy val heading = "Contact name"
  lazy val continueButton = "Continue"
  lazy val emptyErrorMessage = "Error: Enter your Contact name"
  lazy val invalidErrorMessage = "Error: Enter a contact name in the correct format"

  object Selectors {
    val backLink = "body > div > a"
    val caption = "#main-content > div > div > form > span"
    val heading = "#main-content > div > div > form > h1"
    val continueButton   = "#continue"
    val errorMessage = "  #name-value-error"
  }

  "NameView" must {

    val form = Name
      .form()
      .fillAndValidate(Name("Jake"))
    lazy val htmlF = nameView.f(form)(request, messages, mockConfig)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "produce the same output for apply() and render()" in {
      val form = Name
        .form()
        .fillAndValidate(Name("Jake"))
      val htmlApply = nameView.apply(form).body
      val htmlRender = nameView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(nameView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing contact name error correctly " in {
      val form = Name
        .form()
        .fillAndValidate(Name(""))
      val htmlApply = nameView.apply(form).body
      val htmlRender = nameView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(nameView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid contact name error correctly" in {
      val form = Name
        .form()
        .fillAndValidate(Name("!name"))
      val htmlApply = nameView.apply(form).body
      val htmlRender = nameView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(nameView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid contact name error correctly when too many characters have been added" in {
      val form = Name
        .form()
        .fillAndValidate(Name("namenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamename"))
      val htmlApply = nameView.apply(form).body
      val htmlRender = nameView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(nameView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
