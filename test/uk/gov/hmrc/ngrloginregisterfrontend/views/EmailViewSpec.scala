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
import uk.gov.hmrc.ngrloginregisterfrontend.models.Email
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.EmailView

class EmailViewSpec  extends ViewBaseSpec {

  lazy val emailView: EmailView = inject[EmailView]
  lazy val backLink = "Back"
  lazy val caption = "Register for the business rates valuation service"
  lazy val heading = "Enter email address"
  lazy val continueButton = "Continue"
  lazy val emptyErrorMessage = "Error: Enter your email address"
  lazy val invalidErrorMessage = "Error: Enter a valid email address"

  object Selectors {
    val backLink = "#content > a"
    val caption = "#content > form > span"
    val heading = "#content > form > h1"
    val continueButton   = "#continue"
    val errorMessage = "#email-value-error"
  }

  "EmailView" must {

    val form = Email
      .form()
      .fillAndValidate(Email("test@testuser.com"))
    lazy val htmlF = emailView.f(form)(request, messages, mockConfig)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "produce the same output for apply() and render()" in {
      val form = Email
        .form()
        .fillAndValidate(Email("test@testuser.com"))
      val htmlApply = emailView.apply(form).body
      val htmlRender = emailView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(emailView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.continueButton) mustBe continueButton
    }

    "show missing contact email error correctly " in {
      val form = Email
        .form()
        .fillAndValidate(Email(""))
      val htmlApply = emailView.apply(form).body
      val htmlRender = emailView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(emailView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
    "show invalid contact email error correctly " in {
      val form = Email
        .form()
        .fillAndValidate(Email("test@testuser.comtest@testUser.com"))
      val htmlApply = emailView.apply(form).body
      val htmlRender = emailView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(emailView(form)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
      elementText(Selectors.continueButton) mustBe continueButton
    }
  }
}
