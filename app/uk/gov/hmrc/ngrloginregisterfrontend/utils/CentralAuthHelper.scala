/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrloginregisterfrontend.utils

import play.api.http.HeaderNames
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.SessionKeys

object CentralAuthHelper {

  def extractGNAPTokenFromRH(implicit rh: RequestHeader): Option[String] = {
    val sessionAuthToken: Option[String] = rh.session.get(SessionKeys.authToken)
    val authorizationHeader: Option[String] = rh.headers.get(HeaderNames.AUTHORIZATION)

    def gnapParsing(authToken: String): Option[String] = {
      val tokens: Seq[String] = authToken.split(',').toSeq
      tokens.find(t => t.trim.startsWith("GNAP")).map(t => t.replace("GNAP", "").trim)
    }

    (authorizationHeader, sessionAuthToken) match {
      case (_, Some(sessionAuth)) => gnapParsing(sessionAuth)
      case (Some(headerAuth), _) => gnapParsing(headerAuth)
      case _ => None
    }
  }
}
