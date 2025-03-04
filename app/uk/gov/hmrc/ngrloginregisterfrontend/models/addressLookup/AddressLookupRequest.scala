package uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup

import play.api.libs.json.{Format, Json}

case class AddressLookupRequest(postcode: String,
                                filter: Option[String] = None)

object AddressLookupRequest {
  implicit val format: Format[AddressLookupRequest] = Json.format[AddressLookupRequest]
}




