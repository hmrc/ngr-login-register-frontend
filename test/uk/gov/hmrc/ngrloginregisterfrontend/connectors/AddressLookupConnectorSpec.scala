package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import play.api.mvc.Results.Status
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AlfAddress, AlfResponse}

class AddressLookupConnectorSpec extends MockHttpV2 {

  val errorModel: HttpResponse = HttpResponse(Status.BAD_REQUEST, "Error Message")
  val testAddressLookupConnector = new AddressLookupConnector(mockHttp, appConfig)
  lazy val id = "111111111"
  val organisation = "homes ltd"
  val addressLine1 = "line 1"
  val addressLine2 = "line 2"
  val addressLine3 = "line 3"
  val addressLine4 = "line 4"
  val postcode = "aa1 1aa"
  val countryCode = "UK"
  val customerAddressMax: AlfResponse = AlfResponse(
    AlfAddress(
      Some(organisation),
      List(addressLine1, addressLine2, addressLine3, addressLine4),
      Some(postcode),
      Some(countryCode)
    ))

}
