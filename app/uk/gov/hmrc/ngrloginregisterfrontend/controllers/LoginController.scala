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
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ngrloginregisterfrontend.actions.AuthRetrievals
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.LoginView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class LoginController @Inject()( view:LoginView,
                        authRetrievals:AuthRetrievals,
                        mcc: MessagesControllerComponents
                      )(implicit ec: ExecutionContext)extends FrontendController(mcc){

  def start(): Action[AnyContent] = Action.async{ implicit request =>
    authRetrievals.refine(request).map {
      case result =>
        result.affinityGroup.map{ result =>
          result match{
            case AffinityGroup.Agent => println(Console.CYAN_B + ("Agent") + Console.RESET)
            case AffinityGroup.Individual => println(Console.RED_B + ("Individual") + Console.RESET)
            case AffinityGroup.Organisation => println(Console.MAGENTA_B + ("Organisation") + Console.RESET)
          }
        }
        Ok(view(
        nino = result.nino,
        email = result.email,
        name = result.name)
      )

    }
  }
}
