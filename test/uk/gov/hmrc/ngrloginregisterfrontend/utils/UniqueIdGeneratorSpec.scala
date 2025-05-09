package uk.gov.hmrc.ngrloginregisterfrontend.utils

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class UniqueIdGeneratorSpec extends TestSupport {

  private val allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

  "UniqueIdGenerator" must {

    "generate a 12 char id with 2 hyphens" in {
      val id = UniqueIdGenerator.generateId
      id.length mustBe 14
      val compactId = id.replace("-", "")
      compactId.length mustBe 12
      compactId.forall(allowedChars.contains(_)) mustBe true
    }

    "invalidate bad IDs" in {
      val invalidIds = List(
        "0FDE-DFD1-DGJ1",
        "0efkdkfvncma",
        "hello",
        "&fdh-9adf-4jnf"
      )

      invalidIds.foreach { id =>
        withClue(s"Expected '$id' to be invalid: ") {
          UniqueIdGenerator.validateId(id).isLeft mustBe true
        }
      }
    }

    "validate good IDs" in {
      val validIds = List(
        "fdfd-fdfd-dfdf",
        "VDJ4-5NSG-8RHW",
        "BDJ6867MLMNE",
        "nvjf5245bsmv"
      )

      validIds.foreach { id =>
        withClue(s"Expected '$id' to be valid: ") {
          UniqueIdGenerator.validateId(id).isRight mustBe true
        }
      }
    }
  }
}