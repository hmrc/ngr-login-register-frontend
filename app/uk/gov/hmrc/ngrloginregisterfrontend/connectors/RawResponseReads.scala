package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait RawResponseReads {

  implicit val httpReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

}