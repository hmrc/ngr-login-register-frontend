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

package uk.gov.hmrc.ngrloginregisterfrontend.requests

import play.api.i18n.{I18nSupport, Lang, Messages, MessagesApi}
import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.hmrcfrontend.views.Aliases.Language
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

import javax.inject.Inject

class RequestSupport @Inject() (override val messagesApi: MessagesApi) extends I18nSupport {
  implicit def hc(implicit request: Request[_]): HeaderCarrier = RequestSupport.hc
}

object RequestSupport {

  implicit def hc(implicit request: Request[_]): HeaderCarrier = HcProvider.headerCarrier

  /**
   * Naive way of checking if user is logged in. Use it in views only.
   * For more real check see [[service.AuthService]]
   */
  def isLoggedIn(implicit requestHeader: RequestHeader): Boolean = requestHeader.session.get(SessionKeys.authToken).isDefined

  /**
   * Naive way of getting session id. Use it in views only.
   */
  def sessionId(implicit requestHeader: RequestHeader): String = requestHeader.session.get(SessionKeys.sessionId).getOrElse("Unknown Session Id")

  /**
   * This is because we want to give responsibility of creation of [[HeaderCarrier]] to the platform code.
   * If they refactor how hc is created our code will pick it up automatically.
   */
  private object HcProvider extends FrontendHeaderCarrierProvider {
    def headerCarrier(implicit request: Request[_]): HeaderCarrier = hc(request)
  }
}
