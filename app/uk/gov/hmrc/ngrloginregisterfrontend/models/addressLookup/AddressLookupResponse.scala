package uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup

import play.api.libs.json.{Format, Json}

case class AddressLookupResponse (
                                   id: String,
                                   uprn: Int,
                                   parentUprn: Option[Int],
                                   usrn: Option[Int],
                                   organisation: Option[String],
                                   address: Address,
                                   localCustodian: Option[LocalCustodian],
                                   location: Option[Seq[Int]],
                                   language: String,
                                   administrativeArea: Option[String],
                                   poBox: Option[String]
                                 )

object AddressLookupResponse {
  implicit val format: Format[AddressLookupResponse] = Json.format[AddressLookupResponse]
}

  case class Address(
                      lines: Seq[String],
                      town: String,
                      postcode: String,
                      subdivision: Option[Subdivision],
                      country: Subdivision
                    )

object Address {
  implicit val format: Format[Address] = Json.format[Address]
}


  case class Subdivision(
                          code: String,
                          name: String
                        )

object Subdivision {
  implicit val format: Format[Subdivision] = Json.format[Subdivision]
}

  case class LocalCustodian(
                             code: Int,
                             name: String
                           )

object LocalCustodian {
  implicit val format: Format[LocalCustodian] = Json.format[LocalCustodian]
}
