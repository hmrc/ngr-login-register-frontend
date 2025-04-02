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
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.NinoView

class NinoViewSpec  extends ViewBaseSpec {

  lazy val ninoView: NinoView = inject[NinoView]
  lazy val backLink = "Back"
  lazy val caption = "Register for the business rates valuation service"
  lazy val heading = "Provide your National Insurance number"
  lazy val label   = "National Insurance number"
  lazy val hint    = "It’s on your National Insurance card, benefit letter, payslip or P60. For example, ‘QQ 12 34 56 C’"
  lazy val continueButton      = "Continue"
  lazy val emptyErrorMessage   = "Error: Enter your National Insurance number"
  lazy val invalidErrorMessage = "Error: Enter a National Insurance number in the correct format"
  lazy val unmatchedErrorMessage = "Error: Enter a valid National Insurance number"

  object Selectors {
    val backLink = "body > div > a"
    val caption = "#main-content > div > div > form > span"
    val heading = "#main-content > div > div > form > h1"
    val label = "#main-content > div > div > form > div > label"
    val hint = "#nino-value-hint"
    val continueButton   = "#continue"
    val errorMessage = "#nino-value-error"
  }

  "NinoView" must {

    val form = Nino
      .form("AA000003D")
      .fillAndValidate(Nino("AA000003D"))
    lazy val htmlF = ninoView.f(form)(request, messages, mockConfig)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "produce the same output for apply() and render()" in {
      val form = Nino
        .form("AA000003D")
        .fillAndValidate(Nino("AA000003D"))
      val htmlApply = ninoView.apply(form).body
      val htmlRender = ninoView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(ninoView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint)   mustBe hint
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing nino error correctly " in {
      val form = Nino
        .form("")
        .fillAndValidate(Nino(""))
      val htmlApply = ninoView.apply(form).body
      val htmlRender = ninoView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(ninoView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint)   mustBe hint
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show invalid nino error correctly" in {
      val form = Nino
        .form("!nino")
        .fillAndValidate(Nino("!nino"))
      val htmlApply = ninoView.apply(form).body
      val htmlRender = ninoView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(ninoView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint)   mustBe hint
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show valid unmatched nino error correctly" in {
      val form = Nino
        .form("AA000003D")
        .fillAndValidate(Nino("AA000009D"))
      val htmlApply = ninoView.apply(form).body
      val htmlRender = ninoView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(ninoView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.label)   mustBe label
      elementText(Selectors.hint)   mustBe hint
      elementText(Selectors.errorMessage) mustBe unmatchedErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
