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

package uk.gov.hmrc.ngrloginregisterfrontend.config

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.Configuration
import uk.gov.hmrc.ngrloginregisterfrontend.config.features.Features
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class FeatureSpec extends TestSupport{

  private val features = new Features()(app.injector.instanceOf[Configuration])

  override def beforeEach(): Unit = {
    super.beforeEach()
    features.welshLanguageSupportEnabled(true)
  }

  "The welsh language support feature" must {

    "return its current state" in {
      features.welshLanguageSupportEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.welshLanguageSupportEnabled(false)
      features.welshLanguageSupportEnabled() shouldBe false
    }

  }



}
