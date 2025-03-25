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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class StringHelperSpec extends TestSupport with StringHelper {
  "StringHelper" must {
    "mask string with correct unmark characters" in {
      val actual = maskString("QASERTT12345", 5)
      actual shouldBe "*******12345"
    }

    "mask nino string with space correctly" in {
      val actual = maskNino("QQ 12 34 56 C")
      actual shouldBe "******56C"
    }

    "mask nino string without space correctly" in {
      val actual = maskNino("QQ123456C")
      actual shouldBe "******56C"
    }
  }
}