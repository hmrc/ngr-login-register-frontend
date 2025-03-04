package uk.gov.hmrc.ngrloginregisterfrontend.util

trait AddressHelper {

  def generateId: String = {
    java.util.UUID.randomUUID().toString
  }
}