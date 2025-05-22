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

package uk.gov.hmrc.ngrloginregisterfrontend.models.registration

import play.api.libs.json.Json
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.TRN

class RatepayerRegistrationSpec extends TestSupport with TestData {

  "RatepayerRegistrationModel" should {
    "serialise into Json" when {
      "all fields are present" in {
        Json.toJson(testRegistrationModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(TRN, "12345")))) mustBe regResponseJson
      }
      "the optional fields are not present" in {
        Json.toJson(minRegResponseModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(TRN, "12345")))) mustBe minRegResponseJson
      }

    }
    "deserialise from Json" when {
      "all fields are present" in {
        regResponseJson.as[RatepayerRegistration] mustBe testRegistrationModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(TRN, "12345")))
      }
      "the optional fields are not present" in {
        minRegResponseJson.as[RatepayerRegistration] mustBe minRegResponseModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(TRN, "12345")))
      }
    }
  }



}
