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

import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ProvideTRNView

class ProvideTRNControllerSpec extends ControllerSpecSupport{

  lazy val view: ProvideTRNView = inject[ProvideTRNView]

  def controller() =
    new ProvideTRNController(
      view = view,
      isRegisteredCheck = mockIsRegisteredCheck,
      authenticate = mockAuthJourney,
      hasMandotoryDetailsAction = mockHasMandotoryDetailsAction,
      mcc = mcc
    )

  "ProvideTRNController" must {
    "return OK and the correct view for a GET" in {
      val result = controller().show()(ratepayerRegistrationValuationRequest)
      status(result) mustBe OK
    }

    "Calling the submit function return a 303 and the correct redirect location" in {
      val result = controller().submit()(ratepayerRegistrationValuationRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ConfirmUTRController.show.url)
    }
  }
}
