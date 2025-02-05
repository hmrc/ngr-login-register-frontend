package uk.gov.hmrc.ngrloginregisterfrontend.services

import jakarta.inject.Inject
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.CidResponse

import scala.concurrent.{ExecutionContext, Future}

class CitizenDetailsService @Inject()(connector: CitizenDetailsConnector)(implicit ec: ExecutionContext) {

  def getCitizenDetailsResponse(nino: Nino): Future[CidResponse] = {
   for {
     cidResponse <- connector.getCidResponseByNino(nino)
   } yield (
     cidResponse
     )
  }
}
