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
import uk.gov.hmrc.ngrloginregisterfrontend.config.features.Features
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class AppConfigSpec extends TestSupport {

  val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfig" must {

    "initialize features correctly" in {

      appConfig.features shouldBe a[Features] // Ensures Features is initialized
    }

    "retrieve gtmContainer from config" in {

      appConfig.gtmContainer shouldBe "a"
    }

    "retrieve citizenDetailsUrl from ServicesConfig" in {
      appConfig.citizenDetailsUrl shouldBe "http://localhost:9337"
    }

    "retrieve nextGenerationRatesUrl from ServicesConfig" in {

      appConfig.nextGenerationRatesUrl shouldBe "http://localhost:1500"
    }

    "retrieve addressLookupUrl from ServicesConfig" in {

      appConfig.addressLookupUrl shouldBe "http://localhost:9022"
    }

    "retrieve centralAuthServerUrl from ServicesConfig" in {

      appConfig.centralAuthServerUrl shouldBe "http://localhost:15000"
    }

    "retrieve timeToLive from config" in {

      appConfig.timeToLive shouldBe "3"
    }

    "retrieve allowedUserEmailIds from config" in {

      appConfig.allowedUserEmailIds shouldBe List("user@test.com", "66666666email@email.com", "user3@example.com")
    }

    "retrieve dashboard from ServicesConfig" in {

      appConfig.dashboard shouldBe "http://localhost:1503"
    }

    "throw an exception when config key is missing" in {

      val exception = intercept[RuntimeException] {
        appConfig.getString("missing.key")
      }

      exception.getMessage shouldBe "Could not find config key 'missing.key'"
    }
  }
}
