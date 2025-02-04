package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.saUtr
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig

import javax.inject.Inject

class CitizenDetailsConnector @Inject()(http: HttpClientV2,
                                        appConfig: AppConfig) {

  private def url(nino: String) = s"${appConfig.citizenDetailsUrl}/citizen-details/sautr/$saUtr"

}
