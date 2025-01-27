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
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.LoginView

class LoginControllerSpec extends ControllerSpecSupport{

  lazy val loginView = inject[LoginView]

  def controller() =
    new LoginController(
      loginView,
      mockAuthJourney,
      mcc,
    )

  "Login Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().start()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

  }

}
