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

import play.api.data.Form
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.PhoneNumber
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.PhoneNumberView

class PhoneNumberViewSpec extends ViewBaseSpec {

  lazy val phoneNumberView: PhoneNumberView = inject[PhoneNumberView]
  lazy val pageTitle = "Phone Number"

  "PhoneNumberView" must {

    "produce the same output for apply() and render()" in {
      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07954009726"))
      val htmlApply = phoneNumberView.apply(form).body
      val htmlRender = phoneNumberView.render(form, request, messages, mockConfig).body
      htmlApply mustBe htmlRender
    }

    "display information correctly" in {
      def createView(form: Form[PhoneNumber]) = phoneNumberView(form).toString()

      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("07954009726"))
      val view = createView(form)

      view must include(pageTitle)
      view must include("Register for the business rates valuation service")
      view must include("Enter phone number")
      view must include("Phone Number")
      view must include("For international numbers include the country code")

      view must not include("Enter your Phone number")
      view must not include("Please enter a valid phone number")
    }

    "display error messages 'Enter your Phone number' when none is supplied" in {
      def createView(form: Form[PhoneNumber]): String = phoneNumberView(form).toString()

      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber(""))
      val view = createView(form)

      view must include(pageTitle)
      view must include("Enter your Phone number")
    }

    "display error messages 'Please enter a valid phone number' when invalid format is supplied" in {
      def createView(form: Form[PhoneNumber]): String = phoneNumberView(form).toString()

      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("wrong"))
      val view = createView(form)

      view must include(pageTitle)
      view must include("Please enter a valid phone number")
    }

    "display error messages 'Please enter a valid phone number' when number supplied is too long " in {
      def createView(form: Form[PhoneNumber]): String = phoneNumberView(form).toString()

      val form = PhoneNumber
        .form()
        .fillAndValidate(PhoneNumber("1234567891011121314151617"))
      val view = createView(form)

      view must include(pageTitle)
      view must include("Please enter a valid phone number")

    }
  }
}
