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

package uk.gov.hmrc.ngrloginregisterfrontend.mocks

import play.api.Configuration
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.config.features.Features

class MockAppConfig(val runModeConfiguration: Configuration) extends AppConfig {

  override val features: Features = new Features()(runModeConfiguration)
  override val gtmContainer: String = "a"
  override val citizenDetailsUrl: String = "https://localhost:9000"
  override val nextGenerationRatesUrl: String = "https://localhost:1500"
  override def getString(key: String): String = "???"

  override val addressLookupUrl: String = "http://localhost:9000"
  override val centralAuthServerUrl: String = "https://localhost:15000"
  override val timeToLive: String = "3.00"
  override val allowedUserEmailIds: Seq[String] = Seq("user@test.com")
  val dashboard: String = "https://localhost:1503"
  val ngrNotify: String = "https://localhost:1515"
  override val logoutUrl: String = "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:1502/ngr-login-register-frontend/beforeYouGo"
  override val timeout: Int = 900
  override val countdown: Int = 200
  override val feedbackFrontendUrl: String = "http://localhost:9514/feedback/NGR-Dashboard"
  override val publicAccessAllowed: Boolean = false
  override val appName: String = "ngr-login-register-frontend"
}
