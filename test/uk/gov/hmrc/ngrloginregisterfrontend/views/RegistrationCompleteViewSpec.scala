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

import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{Layout, RegistrationCompleteView}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class RegistrationCompleteViewSpec extends ViewBaseSpec {
  val layout: Layout = MockitoSugar.mock[Layout]
  lazy val testView: RegistrationCompleteView = inject[RegistrationCompleteView]
  lazy val backLink = "Back"
  val title = "Registration Successful"
  val heading = "Registration Successful"
  val body1Id = "Your service recovery number is 12345"
  val bodyP2 = "We've sent details about this registration to {0}"
  val bodyP3 = "We've also sent a welcome email which has a guide to using this account."
  val bodyP4 = "Your service recovery number is {0}. You will need this if you have a problem signing in to the service using Government Gateway."
  val bodyH1 = "What happens next"
  val bodyP5 = "Use your Government Gateway ID details next time you sign in to your manage your business rates valuation account"
  val buttonText = "Go to the dashboard"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val headingSelector = "#content > h1"
    val body1Selector = "#content > p:nth-child(0)"
    val body2Selector = "#content > p:nth-child(1)"
    val body3Selector = "#content > p:nth-child(2)"
    val body4Selector = "#content > p:nth-child(3)"
    val body5Selector = "#content > p:nth-child(4)"
    val body6Selector = "#content > p:nth-child(5)"
    val backLink = ".govuk-back-link"
    "The RegistrationCompleteView view" should {
      "Render a page with the appropriate message" when {
        "a recovery ID is present" in {
          lazy implicit val document: Document = Jsoup.parse(testView(Some("12345"))(request, messages, mockConfig).body)
          elementText(Selectors.navTitle) mustBe title
          elementText(Selectors.backLink) mustBe backLink
          elementText(Selectors.headingSelector) mustBe heading
          elementText(Selectors.body1Selector) mustBe body1Id
          elementText(Selectors.body2Selector) mustBe bodyP2
          elementText(Selectors.body3Selector) mustBe bodyP3
          elementText(Selectors.body4Selector) mustBe bodyP4
          elementText(Selectors.body5Selector) mustBe bodyP5
        }
      }
    }
  }
}