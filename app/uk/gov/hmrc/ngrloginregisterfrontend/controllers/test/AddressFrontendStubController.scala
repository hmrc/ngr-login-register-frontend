/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers.test

import com.google.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{Location, LookedUpAddress, LookedUpAddressWrapper, Uprn}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

@Singleton
class AddressFrontendStubController @Inject()(val controllerComponents: MessagesControllerComponents) extends FrontendBaseController with I18nSupport {

  lazy val testAddress = Json.toJson(
    LookedUpAddressWrapper(
      id = "ID1",
      uprn = Uprn(1L),
      address = LookedUpAddress(
        lines = Seq("line 1, line 2, line 3, line 4"), town =  "Telford", county =  Some("Surrey"), postcode = "FX1 7RR"
      ),
      language = "English",
      location = Some(
        Location(
          latitude = 1000,
          longitude = 2000
        )
      )
    )
  )
  def addresses(): Action[AnyContent] = Action { _ =>
    Ok(testAddress)
  }
}
