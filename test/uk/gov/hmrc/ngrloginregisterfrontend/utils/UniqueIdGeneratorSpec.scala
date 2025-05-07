package uk.gov.hmrc.ngrloginregisterfrontend.utils

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class UniqueIdGeneratorSpec extends TestSupport {

  private val allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

  "UniqueIdGenerator" must {
    "generate a 12 char id with 2 hyphens" in {
      val id = UniqueIdGenerator.generateId
      id.length mustBe 14
      id.split("-").mkString.length mustBe 12
      id.split("-").mkString.forall(allowedChars.contains(_))
    }
    "validate an input id" in {
      UniqueIdGenerator.validateId("0FDE-DFD1-DGJ1") match {
        case Left(error) => succeed
        case Right(value) => fail("should be an error")
      }
      UniqueIdGenerator.validateId("0efkdkfvncma") match {
        case Left(error) => succeed
        case Right(value) => fail("should be an error")
      }
      UniqueIdGenerator.validateId("hello") match {
        case Left(error) => succeed
        case Right(value) => fail("should be an error")
      }
      UniqueIdGenerator.validateId("&fdh-9adf-4jnf") match {
        case Left(error) => succeed
        case Right(value) => fail("should be an error")
      }
      UniqueIdGenerator.validateId("fdfd-fdfd-dfdf") match {
        case Left(error) => fail("should be valid")
        case Right(value) => succeed
      }
      UniqueIdGenerator.validateId("VDJ4-5NSG-8RHW") match {
        case Left(error) => fail("should be valid")
        case Right(value) => succeed
      }
      UniqueIdGenerator.validateId("BDJ6867MLMNE") match {
        case Left(error) => fail("should be valid")
        case Right(value) => succeed
      }
      UniqueIdGenerator.validateId("nvjf5245bsmv") match {
        case Left(error) => fail("should be valid")
        case Right(value) => succeed
      }
    }
  }
}
