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

import com.typesafe.config.Config

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
  private val accessibilityHost: String = sc.getConfString(
    confKey = "accessibility-statement.host", throw new Exception("missing config accessibility-statement.host")
  )

  def accessibilityFooterUrl = s"$accessibilityHost/accessibility-statement/ngr-login-register-frontend"
  val addressLookupService: String = sc.baseUrl("address-lookup-frontend")
  val addressLookUpFrontendTestEnabled: Boolean = sc.getBoolean("addressLookupFrontendTest.enabled")
  val addressLookupOffRampUrl: String = sc.getString(key ="addressLookupOffRampUrl")

  object AddressLookupConfig {

    private val addressLookupInitConfig: Config = config
      .getOptional[Configuration](s"address-lookup-frontend-init-config")
      .getOrElse(throw new IllegalArgumentException(s"Configuration for address-lookup-frontend-init-config not found"))
      .underlying

    val version: Int = addressLookupInitConfig.getInt("version")
    val selectPageConfigProposalLimit: Int = addressLookupInitConfig.getInt("select-page-config.proposalListLimit")
  }
}
