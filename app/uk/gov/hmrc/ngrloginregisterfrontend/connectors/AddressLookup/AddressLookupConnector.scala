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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookup

import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupRequest, AddressLookupResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject()(http: HttpClientV2,
                                       appConfig: AppConfig,
                                       logger: NGRLogger)(implicit ec: ExecutionContext) {

  private def url(path: String): URL = url"${appConfig.addressLookupUrl}/address-lookup/$path"


  def findAddressByPostcode(request: AddressLookupRequest)(implicit headerCarrier: HeaderCarrier) : Future[Either[ErrorResponse, Seq[AddressLookupResponse]]] = {
    http.post(url("lookup"))
      .withBody(Json.toJson(request))
      .execute[HttpResponse](readRaw, ec)
      .map { response =>
        response.status match {
          case OK => response.json.validate[AddressLookupResponse] match {
            case JsSuccess(valid, _) => {
              logger.debug("AddressLookupResponse received" + valid)
              Right(Seq(valid))
            }
            case JsError(errors) =>
              Left(ErrorResponse(BAD_REQUEST, s"Json Validation Errors: $errors"))
          }
          case _ => {
            Left(ErrorResponse(response.status, response.body))
          }
        }
      } recover {
      case ex =>
        Left(ErrorResponse(INTERNAL_SERVER_ERROR, s"call to AddressLookup failed: ${ex.getCause} ${ex.getMessage}"))
    }
  }
}
