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
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.InputText

class InputTextSpec extends ViewBaseSpec {
  val form: Form[PhoneNumber] = PhoneNumber.form()

  "InputText" when {
    "produce the same output for apply() and render()" in {
      val htmlApply = InputText(form("phoneNumber.value"))(messages).body
      val htmlF = InputText.f(form("phoneNumber.value"), None, "", None, 0, false, false)(messages).body
      val htmlRender = InputText.render(form("phoneNumber.value"), None, "", None, 0, headingIsLabel = false, autocomplete = false, messages).body
      htmlApply must not be empty
      htmlRender must not be empty
      htmlF must not be empty
    }
  }

}
