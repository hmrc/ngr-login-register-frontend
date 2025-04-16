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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookUpAddresses
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId


class NgrFindAddressRepoSpec extends TestSupport
  with DefaultPlayMongoRepositorySupport[LookUpAddresses] {
  lazy val repository: NgrFindAddressRepo = app.injector.instanceOf[NgrFindAddressRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  val expectedAddress = addressLookupAddress.copy(lines = Seq("Apt 3", "103 Test road"))

  val lookUpAddresses3: LookUpAddresses = LookUpAddresses(credId = credId, createdAt = time, postcode = Postcode("W126WA"),  List(
    addressLookupAddress,
    addressLookupAddress,
    expectedAddress
    )
  )

  "repository" can {
    "save a new LookUpAddresses" when {
      "correct LookUpAddresses has been supplied" in {
        val isSuccessful = await(repository.upsertLookupAddresses(lookUpAddresses3))

        isSuccessful shouldBe true

        val actual = await(repository.findByCredId(credId))

        actual shouldBe Some(lookUpAddresses3)
      }

      "missing credId" in {
        val Addresses: LookUpAddresses = lookUpAddresses3.copy(credId = CredId(null))

        val exception = intercept[IllegalStateException] {
          await(repository.upsertLookupAddresses(Addresses))
        }

        exception.getMessage contains "Addresses have not been inserted" shouldBe true
      }
    }

    "find LookUpAddresses by cred id" when {
      "correct LookUpAddresses has been returned" in {
        await(repository.upsertLookupAddresses(lookUpAddresses3))
        val isSuccessful = await(repository.findByCredId(credId))

        isSuccessful mustBe defined
        val response = isSuccessful.get.copy(createdAt = time)
        val expected = lookUpAddresses3.copy(createdAt = time)
        response shouldBe expected
      }

      "credId doesn't exist in mongoDB" in {
        val actual = await(repository.findByCredId(credId))

        actual mustBe None
      }
    }

    "find a chosen LookedUpAddress by cred id" when {
      "correct LookedUpAddress has been returned" in {
        await(repository.upsertLookupAddresses(lookUpAddresses3))
        val actual = await(repository.findChosenAddressByCredId(credId, 2))

        actual mustBe defined
        actual mustBe Some(expectedAddress)
      }

      "return none when index out of bound" in {
        await(repository.upsertLookupAddresses(lookUpAddresses3))
        val actual = await(repository.findChosenAddressByCredId(credId, 10))

        actual mustBe None
      }
    }
  }
}