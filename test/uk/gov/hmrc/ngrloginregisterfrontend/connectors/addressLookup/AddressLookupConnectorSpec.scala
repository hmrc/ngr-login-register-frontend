package uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup

import play.api.http.Status.OK
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookup.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.MatchingDetails

import scala.concurrent.Future

class AddressLookupConnectorSpec extends MockHttpV2 with TestData {

  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val testAlfConnector: AddressLookupConnector = new AddressLookupConnector(mockHttpClientV2,mockConfig)

  "Calling the Address lookup api" when {
    "a valid AddressLookupRequest is passed in" should {
      "return a 200(OK)" in {
        val successResponse = HttpResponse(status = OK, json = addressLookupResponseJson, headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.addressLookupUrl}/address-lookup/lookup")(successResponse)

      }
    }
  }

}
