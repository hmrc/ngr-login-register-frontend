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

package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{AgentStatus, ReferenceNumber, UserType}

final case class RatepayerRegistration(userType: Option[UserType] = None,
                                 agentStatus: Option[AgentStatus] = None,
                                 name: Option[Name] = None,
                                 tradingName: Option[TradingName] = None,
                                 email: Option[Email] = None,
                                 contactNumber: Option[ContactNumber] = None,
                                 secondaryNumber: Option[ContactNumber] = None,
                                 address: Option[Address] = None,
                                 referenceNumber: Option[ReferenceNumber] = None,
                                 isRegistered: Option[Boolean] = Some(false)
                                )


object RatepayerRegistration {

  implicit val format: Format[RatepayerRegistration] = Json.format[RatepayerRegistration]

}
