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

import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{MatchingDetails, PersonDetails}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import scala.concurrent.Future
import play.api.libs.json._
import uk.gov.hmrc.domain.Nino

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CitizenDetailsConnector @Inject()(http: HttpClientV2,
                                        appConfig: AppConfig)
                                       (implicit ec: ExecutionContext) {

  def getMatchingResponse(nino: Nino)(implicit headerCarrier: HeaderCarrier): Future[Either[ErrorResponse, MatchingDetails]] = {
    http.get(url"${appConfig.citizenDetailsUrl}/citizen-details/nino/${nino.value}")
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[MatchingDetails] match {
              case JsSuccess(valid, _) => Right(valid)
              case JsError(errors) =>
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case _ =>
            Left(ErrorResponse(response.status, response.body))
        }
      } recover {
      case _ =>
        Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, s"Call to citizen details failed"))
      }
  }

  def getPersonDetails(nino: Nino)(implicit headerCarrier: HeaderCarrier): Future[Either[ErrorResponse, PersonDetails]] = {
    http.get(url"${appConfig.citizenDetailsUrl}/citizen-details/${nino.value}/designatory-details")
      .execute[HttpResponse](readRaw, ec)
      .map { response =>
        response.status match {
          case OK =>
            response.json.validate[PersonDetails] match {
              case JsSuccess(valid, _) => Right(valid)
              case JsError(errors) =>
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case _ =>
            Left(ErrorResponse(response.status, response.body))

        }
      }recover {
      case _ =>
        Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, s"Call to citizen details failed"))
    }
  }
}
