package uk.gov.hmrc.ngrloginregisterfrontend.session

import play.api.mvc.{Result, RequestHeader}
import java.util.UUID
import javax.inject.Singleton

@Singleton
class SessionManager {

  val journeyIdKey = "NGR-JourneyId"

  private def addToSession(result: Result, key: String, value: String)(implicit requestHeader: RequestHeader) = {
    result.addingToSession(key -> value)
  }

  private def removeFromSession(result: Result, key: String)(implicit requestHeader: RequestHeader) = {
    result.removingFromSession(key)
  }

  private def getFromSession(result: Result, key: String)(implicit requestHeader: RequestHeader): Option[String] = {
    result.session.get(key)
  }

  def setJourneyId(result: Result, journeyId: String)(implicit requestHeader: RequestHeader) = {
    addToSession(result.withNewSession, journeyIdKey, journeyId)
  }

  def generateJourneyId: String = {
    UUID.randomUUID().toString
  }
}