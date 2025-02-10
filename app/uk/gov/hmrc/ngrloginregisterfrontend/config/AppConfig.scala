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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.ngrloginregisterfrontend.config.features.Features
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val features: Features
  val gtmContainer: String
  val citizenDetailsUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, sc: ServicesConfig) extends AppConfig {
  override val features = new Features()(config)
  override val gtmContainer: String = sc.getString("tracking-consent-frontend.gtm.container")
  override val citizenDetailsUrl: String = sc.baseUrl("citizen-details")
}
