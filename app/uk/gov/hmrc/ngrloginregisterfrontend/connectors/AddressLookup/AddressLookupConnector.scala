package uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookup

import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupRequest, AddressLookupResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject()(http: HttpClientV2,
                                       appConfig: AppConfig,
                                       logger: NGRLogger)(implicit ec: ExecutionContext, headerCarrier: HeaderCarrier) {

  private def url(path: String): URL = url"${appConfig.addressLookupUrl}/address-lookup/$path"


  def findAddressByPostcode(request: AddressLookupRequest): Future[Either[ErrorResponse, AddressLookupResponse]] = {
    http.post(url("lookup"))
      .withBody(Json.toJson(request))
      .execute[HttpResponse](readRaw, ec)
      .map { response =>
        response.status match {
          case OK => response.json.validate[AddressLookupResponse] match {
            case JsSuccess(valid, _) => {
              logger.debug("AddressLookupResponse received" + valid)
              Right(valid)
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
