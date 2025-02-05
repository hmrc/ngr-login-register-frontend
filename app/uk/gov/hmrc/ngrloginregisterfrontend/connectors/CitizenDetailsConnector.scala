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
