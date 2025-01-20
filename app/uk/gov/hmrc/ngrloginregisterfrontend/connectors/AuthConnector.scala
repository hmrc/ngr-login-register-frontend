package uk.gov.hmrc.ngrloginregisterfrontend.connectors

/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.auth.core.PlayAuthConnector
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AuthConnector @Inject()(override val http: HttpClientV2, servicesConfig: ServicesConfig) extends PlayAuthConnector {
  override val serviceUrl: String = servicesConfig.baseUrl("auth")
}