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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.RegistrationCompleteView

class RegistrationCompleteControllerSpec extends ControllerSpecSupport {

  lazy val RegistrationCompleteRecoveryId: String = routes.RegistrationCompleteController.show(Some("12345xyz")).url
  lazy val testView: RegistrationCompleteView = inject[RegistrationCompleteView]

  val pageTitle = "Registration Successful"

  def controller() = new RegistrationCompleteController(
    testView,
    mcc
  )

  "RegistrationComplete Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show(Some("12345xyz"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
