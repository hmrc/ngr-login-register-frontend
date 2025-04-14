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

package uk.gov.hmrc.ngrloginregisterfrontend.repo

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookUpAddresses
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId

import java.time.Instant


class NgrFindAddressRepoSpec extends TestSupport with TestData
  with DefaultPlayMongoRepositorySupport[LookUpAddresses] {
  lazy val repository: NgrFindAddressRepo = app.injector.instanceOf[NgrFindAddressRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  val time = Instant.now()

  private val credId: CredId = CredId("12345")

  private val lookUpAddresses: LookUpAddresses = LookUpAddresses(credId = credId, createdAt = time,  List(
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress)
  )

  private val lookUpAddresses2: LookUpAddresses = LookUpAddresses(credId = credId, createdAt = time, List(
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress)
  )



  "repository" can {
    "save a new ratepayerRegistration" when {
      "correct ratepayer has been supplied" in {
        val isSuccessful = await(repository.upsert(lookUpAddresses))

        isSuccessful shouldBe true

        val actual = await(repository.findByCredId(credId)).get

        val actualWithoutTimestamp = actual.copy(createdAt = time)
        val expectWithoutTimeStamp = lookUpAddresses.copy(createdAt = time)

        actualWithoutTimestamp shouldBe expectWithoutTimeStamp
      }

      "missing credId" in {
        val Addresses: LookUpAddresses = lookUpAddresses.copy(credId = CredId(null))

        val exception = intercept[IllegalStateException] {
          await(repository.upsert(Addresses))
        }

        exception.getMessage contains "Addresses have not been inserted" shouldBe true
      }
    }
  }


}