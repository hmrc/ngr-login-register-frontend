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

//    "find a ratepayer by cred id" when {
//      "correct ratepayer has been returned" in {
//        await(repository.upsert(ratepayerRegistrationValuation))
//        val isSuccessful = await(repository.findByCredId(credId))
//
//        val respoonse = isSuccessful.get.copy(createdAt = None)
//        val expected = ratepayerRegistrationValuation.copy(createdAt = None)
//        respoonse shouldBe expected
//      }
//    }
//
//    "update ratepayerRegistration" when {
//      "the same credId ratepayer has been supplied" in {
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//        isSuccessful shouldBe true
//
//        val isSuccessful2 = await(repository.upsert(ratepayerRegistrationValuation2))
//        isSuccessful2 shouldBe true
//
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//        val actualWithoutTimestamp = actual.copy(createdAt = None)
//        val expectWithoutTimeStamp = ratepayerRegistrationValuation2.copy(createdAt = None)
//
//        actualWithoutTimestamp shouldBe expectWithoutTimeStamp
//      }
//    }
//
//    "update phone number" when {
//      "a phone has been supplied" in {
//        val contactNumber: ContactNumber = ContactNumber("07702467254")
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateContactNumber(credId, contactNumber))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.contactNumber shouldBe Some(contactNumber)
//      }
//    }
//
//    "update nino" when {
//      "a nino has been supplied" in {
//        val nino: Nino = Nino("AA000003D")
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateNino(credId, nino))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.nino shouldBe Some(nino)
//      }
//    }
//
//    "update name" when {
//      "a name has been supplied" in {
//        val name: Name = Name("Anna S")
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateName(credId, name))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.name shouldBe Some(name)
//      }
//    }
//
//    "update email" when {
//      "an email has been supplied" in {
//        val email: Email = Email("annaS@annaltd.com")
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateEmail(credId, email))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.email shouldBe Some(email)
//      }
//    }
//
//    "update address" when {
//      "an address has been supplied" in {
//        val address: Address = Address("Address Line 1", Some("Line 2"), "Chester", None, Postcode("CH2 1HW"))
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateAddress(credId, address))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.address shouldBe Some(address)
//      }
//    }
//
//    "update TRN" when {
//      "a TRN has been supplied" in {
//        val referenceNumber: TRNReferenceNumber = TRNReferenceNumber(TRN, "34567821")
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.updateTRN(credId, referenceNumber))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.trnReferenceNumber shouldBe Some(referenceNumber)
//      }
//    }
//
//    "update isRegistered" when {
//      "isRegistered has been called" in {
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.setIsRegistered(credId))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.isRegistered shouldBe Some(true)
//      }
//    }
//    "find ratepayerRegistration" when {
//      "existing credId in mongoDB" in {
//        val isSuccessful = await(repository.upsert(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        val actual = await(repository.findAndUpdateByCredId(credId))
//
//        actual.isDefined shouldBe true
//        actual.get.ratepayerRegistration.get shouldBe ratepayerRegistration
//      }
//
//      "credId doesn't exist in mongoDB" in {
//        val actual = await(repository.findAndUpdateByCredId(credId))
//
//        actual shouldBe None
//      }
//    }
  }


}