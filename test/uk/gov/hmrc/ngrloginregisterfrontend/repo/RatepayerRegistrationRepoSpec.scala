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
import play.api.test.Helpers
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Postcode, RatepayerRegistration, TradingName}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Address, Email, Name, Nino, PhoneNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.AgentStatus.Autonomous
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.UserType.Individual

import java.time.Instant
import scala.concurrent.ExecutionContext


class RatepayerRegistrationRepoSpec extends TestSupport
  with DefaultPlayMongoRepositorySupport[RatepayerRegistrationValuation] {
  lazy val repository: RatepayerRegistrationRepo = app.injector.instanceOf[RatepayerRegistrationRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  private val ratepayerRegistration: RatepayerRegistration = RatepayerRegistration(Some(Individual), Some(Autonomous), Some(Name("Jake")),
    Some(TradingName("Jake Ltd.")), Some(Email("jake.r@jakeltd.com")), Some(Nino("AA000003D")), Some(PhoneNumber("07702467839")), None,
    Some(Address("Address Line 1", None, "Cambridge", None, Postcode("CB2 1HW"))), None, Some(false))

  private val ratepayerRegistration2: RatepayerRegistration = RatepayerRegistration(Some(Individual), Some(Autonomous), Some(Name("Anna")),
    Some(TradingName("Anna Ltd.")), Some(Email("Anna.s@annaltd.com")), Some(Nino("AA000003D")), Some(PhoneNumber("08707632451")), None,
    Some(Address("Address Line 1", None, "Chester", None, Postcode("CH2 7RH"))), None, Some(false))

  private val ratepayerRegistrationValuation: RatepayerRegistrationValuation = RatepayerRegistrationValuation.apply(credId, Some(ratepayerRegistration))

  private val ratepayerRegistrationValuation2: RatepayerRegistrationValuation = RatepayerRegistrationValuation.apply(credId, Some(ratepayerRegistration2))

  "repository" can {
    "save a new ratepayerRegistration" when {
      "correct ratepayer has been supplied" in {
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful mustBe  true

        val actual = await(repository.findAndUpdateByCredId(credId)).get
        val actualWithoutTimestamp = actual
        val expectWithoutTimeStamp = ratepayerRegistrationValuation

        actualWithoutTimestamp shouldBe expectWithoutTimeStamp
      }
    }

    "find a ratepayer by cred id" when {
      "correct ratepayer has been returned" in {
        await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))
        val isSuccessful = await(repository.findByCredId(credId))

        val respoonse = isSuccessful.get
        val expected = ratepayerRegistrationValuation
        respoonse shouldBe expected
      }
    }

    "update ratepayerRegistration" when {
      "the same credId ratepayer has been supplied" in {
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))
        isSuccessful shouldBe true

        val isSuccessful2 = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation2))
        isSuccessful2 shouldBe true

        val actual = await(repository.findAndUpdateByCredId(credId)).get
        val actualWithoutTimestamp = actual
        val expectWithoutTimeStamp = ratepayerRegistrationValuation2

        actualWithoutTimestamp shouldBe expectWithoutTimeStamp
      }
    }

    "update phone number" when {
      "a phone has been supplied" in {
        val contactNumber: PhoneNumber = PhoneNumber("07702467254")
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful mustBe true

        await(repository.updateContactNumber(credId, contactNumber))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.contactNumber shouldBe Some(contactNumber)
      }
    }

    "update nino" when {
      "a nino has been supplied" in {
        val nino: Nino = Nino("AA000003D")
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful mustBe true

        await(repository.updateNino(credId, nino))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.nino shouldBe Some(nino)
      }
    }

    "update name" when {
      "a name has been supplied" in {
        val name: Name = Name("Anna S")
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful shouldBe true

        await(repository.updateName(credId, name))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.name shouldBe Some(name)
      }
    }

    "update email" when {
      "an email has been supplied" in {
        val email: Email = Email("annaS@annaltd.com")
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful shouldBe true

        await(repository.updateEmail(credId, email))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.email shouldBe Some(email)
      }
    }

//    "update address" when {
//      "an address has been supplied" in {
//        val address: Address = Address("Address Line 1", Some("Line 2"), "Chester", None, Postcode("CH2 1HW"))
//        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))
//
//        isSuccessful shouldBe true
//
//        await(repository.upsertRatepayerRegistration(credId, address))
//        val actual = await(repository.findAndUpdateByCredId(credId)).get
//
//        actual.ratepayerRegistration.get.address shouldBe Some(address)
//      }
//    }

    "update TRN" when {
      "a TRN has been supplied" in {
        val referenceNumber: TRNReferenceNumber = TRNReferenceNumber(TRN, "34567821")
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful shouldBe true

        await(repository.updateTRN(credId, referenceNumber))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.trnReferenceNumber shouldBe Some(referenceNumber)
      }
    }

    "update isRegistered" when {
      "isRegistered has been called" in {
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful mustBe true

        await(repository.registerAccount(credId))
        val actual = await(repository.findAndUpdateByCredId(credId)).get

        actual.ratepayerRegistration.get.isRegistered shouldBe Some(true)
      }
    }
    "find ratepayerRegistration" when {
      "existing credId in mongoDB" in {
        val isSuccessful = await(repository.upsertRatepayerRegistration(ratepayerRegistrationValuation))

        isSuccessful shouldBe true

        val actual = await(repository.findAndUpdateByCredId(credId))

        actual.isDefined shouldBe true
        actual.get.ratepayerRegistration.get shouldBe ratepayerRegistration
      }

      "credId doesn't exist in mongoDB" in {
        val actual = await(repository.findAndUpdateByCredId(credId))

        actual shouldBe None
      }
    }
  }


}