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

import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.centralauth.{TokenAttributesRequest, TokenAttributesResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CentralAuthConnector @Inject()(http: HttpClientV2,
                                     appConfig: AppConfig,
                                     logger: NGRLogger )
                                    (implicit ec: ExecutionContext){

  def getTokenAttributesResponse(gnapToken: String)(implicit headerCarrier: HeaderCarrier) : Future[Either[ErrorResponse, TokenAttributesResponse]] = {
    val model: TokenAttributesRequest = TokenAttributesRequest(gnapToken)
    http.post(url"${appConfig.centralAuthServerUrl}/centralised-authorisation-server/token/search")
      .withBody(Json.toJson(model))
      .execute[HttpResponse](readRaw, ec)
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[TokenAttributesResponse] match {
              case JsSuccess(valid, _) => {
                logger.debug("[CentralAuthConnector][getTokenAttributeResponse] Response received")
                Right(valid)
              }
              case JsError(errors) =>
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case _ => {
            Left(ErrorResponse(response.status, response.body))
          }

        }
      } recover {
      case _ =>
        Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to Central Auth Server Failed"))
    }
  }

}
