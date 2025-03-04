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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import play.api.libs.json.Json
import uk.gov.hmrc.hmrcfrontend.config
import uk.gov.hmrc.http
import uk.gov.hmrc.http.HttpReadsInstances.readFromJson
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.mongo.lock.Lock.id
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.httpParsers.ResponseHttpParser.HttpResult
import uk.gov.hmrc.ngrloginregisterfrontend.models.AlfResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.JourneyConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector@Inject()(val http: HttpClientV2,
                                       implicit val config: AppConfig) {

  private[connectors] def getAddressUrl(id: String, addressLookupFrontendTestEnabled: Boolean): String = {
      s"${config.addressLookupService}/api/confirmed?id=$id"
    }

  private[connectors] def initJourneyUrl(addressLookupFrontendTestEnabled: Boolean): String = {
    s"${config.addressLookupService}/api/init"
  }

  def getAddress(alfId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[String]] = {
    http.get(url"${config.addressLookupService}/api/confirmed?id=$id")
      .execute[HttpResult[AlfResponse]]
  }

  def initJourney(journeyConfig: JourneyConfig)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[String]] = {
    http.post(url"${initJourneyUrl}")
      .withBody(Json.toJson(journeyConfig))
      .execute[HttpResult[String]]
  }
}
