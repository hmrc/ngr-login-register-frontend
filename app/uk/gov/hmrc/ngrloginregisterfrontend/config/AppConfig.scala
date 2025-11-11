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

import play.api.Configuration
import uk.gov.hmrc.ngrloginregisterfrontend.config.features.Features
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

trait AppConfig {
  val features: Features
  val logoutUrl:String
  val gtmContainer: String
  val citizenDetailsUrl: String
  val nextGenerationRatesUrl: String
  def getString(key: String): String
  val addressLookupUrl: String
  val centralAuthServerUrl: String
  val timeToLive: String
  val dashboard: String
  val ngrNotify: String
  val timeout: Int
  val countdown: Int
  val feedbackFrontendUrl:String
}

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, sc: ServicesConfig) extends AppConfig {
  override val features = new Features()(config)
  override val timeToLive: String = sc.getString("time-to-live.time")
  override val gtmContainer: String = sc.getString("tracking-consent-frontend.gtm.container")
  override val citizenDetailsUrl: String = sc.baseUrl("citizen-details")
  override val nextGenerationRatesUrl: String = sc.baseUrl("next-generation-rates")
  override val addressLookupUrl: String = sc.baseUrl("address-lookup")
  override val centralAuthServerUrl: String = sc.baseUrl("centralised-authorisation-server")
  override val dashboard: String = sc.baseUrl("ngr-dashboard-frontend")
  override val ngrNotify: String = sc.baseUrl("ngr-notify")
  private lazy val basGatewayHost = getString("microservice.services.bas-gateway-frontend.host")
  lazy val logoutUrl: String = s"$basGatewayHost/bas-gateway/sign-out-without-state?continue=$registrationBeforeYouGoUrl"
  private lazy val envHost = getString("environment.host")
  private lazy val registrationBeforeYouGoUrl: String = s"$envHost${routes.BeforeYouGoController.show.url}"
  private lazy val feedbackFrontendHost = getString("microservice.services.feedback-survey-frontend.host")
  lazy val feedbackFrontendUrl: String = s"$feedbackFrontendHost/feedback/Next-Generation-Rates"


  def getString(key: String): String =
    config.getOptional[String](key)
      .getOrElse(throwConfigNotFoundError(key))

  private def throwConfigNotFoundError(key: String): String =
    throw new RuntimeException(s"Could not find config key '$key'")

  override val timeout: Int = config.get[Int]("timeout-dialog.timeout")
  override val countdown: Int = config.get[Int]("timeout-dialog.countdown")
}
