package uk.gov.hmrc.ngrloginregisterfrontend.connectors.httpParsers

import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse

trait object ResponseHttpParser {
  type HttpResult[T] = Either[ErrorResponse, T]
}