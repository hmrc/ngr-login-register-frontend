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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.{FeatureNotImplementedController, routes}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FeatureNotImplementedView

class FeatureNotImplementedControllerSpec extends ControllerSpecSupport {

  lazy val testView: FeatureNotImplementedView = inject[FeatureNotImplementedView]

 val pageTitle = "This part of the online service is not available yet"

  def controller() = new FeatureNotImplementedController(
    testView,
    mcc
  )

  "FeatureNotImplemented Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show(Some("12345"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
