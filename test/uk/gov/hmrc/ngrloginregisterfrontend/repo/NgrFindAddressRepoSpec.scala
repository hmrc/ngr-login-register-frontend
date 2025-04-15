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
import play.api.test.Helpers.await
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.LookUpAddresses
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId

import java.time.Instant


class NgrFindAddressRepoSpec extends TestSupport
  with DefaultPlayMongoRepositorySupport[LookUpAddresses] {
  lazy val repository: NgrFindAddressRepo = app.injector.instanceOf[NgrFindAddressRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  val lookUpAddresses3: LookUpAddresses = LookUpAddresses(credId = credId, createdAt = time, postcode = Postcode("W126WA"),  List(
    addressLookupAddress,
    addressLookupAddress,
    addressLookupAddress)
  )

  "repository" can {
    "save a new ratepayerRegistration" when {
      "correct ratepayer has been supplied" in {
        val isSuccessful = await(repository.upsertLookupAddresses(lookUpAddresses3))

        isSuccessful shouldBe true

        val actual = await(repository.findByCredId(credId))

        actual shouldBe lookUpAddresses3
      }

      "missing credId" in {
        val Addresses: LookUpAddresses = lookUpAddresses3.copy(credId = CredId(null))

        val exception = intercept[IllegalStateException] {
          await(repository.upsertLookupAddresses(Addresses))
        }

        exception.getMessage contains "Addresses have not been inserted" shouldBe true
      }
    }

    "find a ratepayer by cred id" when {
      "correct ratepayer has been returned" in {
        await(repository.upsertLookupAddresses(lookUpAddresses3))
        val isSuccessful = await(repository.findByCredId(credId))

        val respoonse = isSuccessful.copy(createdAt = time)
        val expected = lookUpAddresses3.copy(createdAt = time)
        respoonse shouldBe expected
      }
    }

    "credId doesn't exist in mongoDB" in {
      val actual = await(repository.findByCredId(credId))
       val response =  actual.copy(createdAt = time)

      response shouldBe LookUpAddresses(credId = credId, createdAt = time, postcode = Postcode(""))
    }
  }
}