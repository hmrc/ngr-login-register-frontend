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

package uk.gov.hmrc.ngrloginregisterfrontend.utils

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.Results.Redirect
import play.api.mvc.{Result, Session}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport

class SessionTimeoutHelperSpec extends ControllerSpecSupport with SessionTimeoutHelper {
  private val session: Session = Session()

  "StringHelperSpec" must {
    "Return Right with the value when the key exists in the session" in {
      when(mockSessionManager.getSessionValue(any(), any())).thenReturn(Some("A String value"))
      val result: Either[Result, Option[String]] = getSession(mockSessionManager, session, "key1")
      result mustBe Right(Some("A String value"))
    }

    "Return Left with Result redirect to confirm contact details when the key doesn't exist in the session" in {
      when(mockSessionManager.getSessionValue(any(), any())).thenReturn(None)
      val result: Either[Result, Option[String]] = getSession(mockSessionManager, session, "key1")
      result mustBe Left(Redirect(routes.ConfirmContactDetailsController.show))
    }
  }
}