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

import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NgrNotifyConnector @Inject()(
                                               http: HttpClientV2,
                                               appConfig: AppConfig
                                             )(implicit ec: ExecutionContext) {

  def isAllowedInPrivateBeta(credId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"${appConfig.ngrNotify}/allowed-in-private-beta/$credId"
    http.get(url)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            (response.json \ "allowed").asOpt[Boolean].getOrElse(false)
          case _ =>
            false
        }
      } recover {
      case _ => false
    }
  }
}