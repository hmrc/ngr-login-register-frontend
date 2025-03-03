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

import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.HttpReads.Implicits.readFromJson
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, ReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, ContactNumber, Email, Name, RatepayerRegistration}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NGRConnector @Inject()(http: HttpClientV2,
                             appConfig: AppConfig,
                             logger: NGRLogger)
                            (implicit ec: ExecutionContext) {

  private def url(path: String): URL = url"${appConfig.nextGenerationRatesUrl}/next-generation-rates/$path"

  def upsertRatepayer(model: RatepayerRegistrationValuation)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.post(url("upsert-ratepayer"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Upsert Ratepayer" + response.body)
        response.status match {
          case CREATED => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }


  def getRatepayer(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[RatepayerRegistrationValuation]] = {
    implicit val rds: HttpReads[RatepayerRegistrationValuation] = readFromJson
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, None)
    http.get(url("get-ratepayer"))
      .withBody(Json.toJson(model))
      .execute[Option[RatepayerRegistrationValuation]].recoverWith {
      case UpstreamErrorResponse(_, NOT_FOUND, _ , _) => Future.successful(None)
    }
  }

  def changeName(credId: CredId, name: Name)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val ratepayer: RatepayerRegistration = RatepayerRegistration(name = Some(name))
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
    http.post(url("change-name"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Change name" + response.body)
        response.status match {
          case OK => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def changePhoneNumber(credId: CredId, contactNumber: ContactNumber)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val ratepayer: RatepayerRegistration = RatepayerRegistration(contactNumber = Some(contactNumber))
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
    http.post(url("change-phone-number"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Change phone number" + response.body)
        response.status match {
          case OK => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def changeEmail(credId: CredId, email: Email)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val ratepayer: RatepayerRegistration = RatepayerRegistration(email = Some(email))
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
    http.post(url("change-email"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Change email" + response.body)
        response.status match {
          case OK => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def changeTrn(credId: CredId, trn: ReferenceNumber)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val ratepayer: RatepayerRegistration = RatepayerRegistration(referenceNumber = Some(trn))
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
    http.post(url("change-trn"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Change trn" + response.body)
        response.status match {
          case OK => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def changeAddress(credId: CredId, address: Address)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val ratepayer: RatepayerRegistration = RatepayerRegistration(address = Some(address))
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
    http.post(url("change-address"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Change address" + response.body)
        response.status match {
          case OK => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def findAddress(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[Address]] = {
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, None)
    http.post(url("find-address"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Find address" + response.body)
        response.status match {
          case OK => Json.parse(response.body).validateOpt[Address] match {
            case JsSuccess(addressOpt, _) => addressOpt
            case JsError(errors) =>
              logger.error(s"Failed to parse address: $errors")
              None
          }
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

  def isRegistered(credId: CredId)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId)
    http.post(url("is-registered"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Is registered" + response.body)
        response.status match {
          case OK => true
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
      }
  }

}
