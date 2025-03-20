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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup

import play.api.i18n.Lang.logger.warn
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.AddressLookupResponseModel
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

sealed trait AddressLookupResponse

case class AddressLookupSuccessResponse(addressList: AddressLookupResponseModel) extends AddressLookupResponse
case class AddressLookupErrorResponse(cause: Exception) extends AddressLookupResponse

class AddressLookupConnector @Inject()(http: HttpClientV2,
                                       appConfig: AppConfig,
                                       logger: NGRLogger) {

  private def url(path: String): URL = url"${appConfig.addressLookupUrl}/address-lookup/$path"

  implicit class JsObjectOps(json: JsObject) {
    def +?(o: Option[JsObject]): JsObject = o.fold(json)(_ ++ json)
  }

  def findAddressByPostcode(postcode: String, filter: Option[String])(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[AddressLookupResponse] = {
    http.post(url("lookup"))
      .withBody(Json.obj("postcode" -> postcode) +? filter.map(f => Json.obj("filter" -> f)))
      .setHeader("X-Hmrc-Origin" -> "ngr-login-register-frontend")
      .execute[JsValue] map {
      addressListJson =>
        logger.info(s"Successfully Received addressList $addressListJson")
        AddressLookupSuccessResponse(AddressLookupResponseModel.fromJsonAddressLookupService(addressListJson))
    } recover {
      case e: Exception =>
        logger.warn(s"Error received from address lookup service: $e")
        AddressLookupErrorResponse(e)
    }
  }
}
