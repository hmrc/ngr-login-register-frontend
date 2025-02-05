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

import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpException, NotFoundException, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.MatchingDetails

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CitizenDetailsConnector @Inject()(http: HttpClientV2,
                                        appConfig: AppConfig)
                                       (implicit ec: ExecutionContext,
                                        hc: HeaderCarrier) extends RawResponseReads {

  def getCidResponseByNino(nino: Nino): Future[MatchingDetails] = {
    http.get(url"${appConfig.citizenDetailsUrl}/citizen-details/nino/$nino").execute[MatchingDetails]
  }.recover {
    case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND =>
      throw new NotFoundException("No UTR found on CID")
    case e: UpstreamErrorResponse if e.statusCode == BAD_REQUEST =>
      throw new BadRequestException("Call to Cid failed, Nino is invalid")
    case e: UpstreamErrorResponse =>
      throw new HttpException(e.getMessage, e.statusCode)
  }

}
