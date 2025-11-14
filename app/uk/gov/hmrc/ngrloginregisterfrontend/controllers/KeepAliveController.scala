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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KeepAliveController @Inject()(
                                     val controllerComponents: MessagesControllerComponents,
                                     authenticate: AuthRetrievals,
                                     isRegisteredCheck: RegistrationAction,
                                     hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                     repository: RatepayerRegistrationRepo
                                   )(implicit  ec: ExecutionContext) extends FrontendBaseController {

  def keepAlive(): Action[AnyContent] = (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async {
    implicit request =>
     repository.keepAlive(request.credId).map(_ => Ok)
  }
}
