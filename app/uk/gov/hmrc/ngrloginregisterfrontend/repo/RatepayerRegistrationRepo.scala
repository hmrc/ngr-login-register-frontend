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

import com.google.inject.Singleton
import com.mongodb.client.model.Indexes.descending
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.Updates.combine
import org.mongodb.scala.model._
import play.api.Logging
import play.api.mvc.{Result, Results}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.ngrloginregisterfrontend.config.FrontendAppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, TRNReferenceNumber}

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
case class RatepayerRegistrationRepo @Inject()(mongo: MongoComponent,
                                               config: FrontendAppConfig
                                        )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[RatepayerRegistrationValuation](
    collectionName = "ratepayerRegistration",
    mongoComponent = mongo,
    domainFormat = RatepayerRegistrationValuation.format,
    indexes = Seq(
      IndexModel(
        descending("createdAt"),
        IndexOptions()
          .unique(false)
          .name("createdAt")
          .expireAfter(config.timeToLive.toLong, TimeUnit.HOURS)
      ),
      IndexModel(
        ascending("credId.value"),
        IndexOptions()
          .background(false)
          .name("credId.value")
          .unique(true)
          .partialFilterExpression(Filters.gte("credId.value", ""))
      )
    )
  ) with Logging {

  override lazy val requiresTtlIndex: Boolean = false

  private def filterByCredId(credId: CredId): Bson = equal("credId.value", credId.value)

  def upsertRatepayerRegistration(registration: RatepayerRegistrationValuation): Future[Boolean] = {
    val errorMsg = s"Addresses have not been inserted"

    collection.replaceOne(
      filter = equal("credId.value", registration.credId.value),
      replacement = registration,
      options = ReplaceOptions().upsert(true)
    ).toFuture().transformWith {
      case Success(result) =>
        logger.info(s"RatepayerRegistration has been upsert for credId: ${registration.credId.value}")
        result.wasAcknowledged()
        Future.successful(true)
      case Failure(exception) =>
        logger.error(errorMsg)
        Future.failed(new IllegalStateException(s"$errorMsg: ${exception.getMessage} ${exception.getCause}"))
    }
  }



  def findAndUpdateByCredId(credId: CredId, updates: Bson*): Future[Option[RatepayerRegistrationValuation]] = {
    collection.findOneAndUpdate(filterByCredId(credId),
        combine(updates :+ Updates.set("createdAt", Instant.now()): _*),
        FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))
      .toFutureOption()
  }

  def findByCredId(credId: CredId): Future[Option[RatepayerRegistrationValuation]] = {
    collection.find(
      equal("credId.value", credId.value)
    ).headOption()
  }

  def deleteRecord(credId: CredId):Future[Result] = {
    collection.deleteOne(
      equal("credId.value", credId.value)
    ).toFuture().map(_ => Results.Ok(s"User $credId info dropped from frontend")).recover { case e =>
      e.printStackTrace()
      Results.InternalServerError(e.toString)
    }
  }

  def updateContactNumber(credId: CredId, contactNumber: PhoneNumber): Future[Option[RatepayerRegistrationValuation]] = {
    findAndUpdateByCredId(credId, Updates.set("ratepayerRegistration.contactNumber.value", contactNumber.value))
  }

  def updateName(credId: CredId, name: Name): Future[Option[RatepayerRegistrationValuation]] = {
    findAndUpdateByCredId(credId, Updates.set("ratepayerRegistration.name.value", name.value))
  }

  def updateEmail(credId: CredId, email: Email): Future[Option[RatepayerRegistrationValuation]] = {
    findAndUpdateByCredId(credId, Updates.set("ratepayerRegistration.email.value", email.value))
  }

  def updateAddress(credId: CredId, address: Address): Future[Option[RatepayerRegistrationValuation]] = {
    val updates = Seq(
      Updates.set("ratepayerRegistration.address.line1", address.line1),
      Updates.set("ratepayerRegistration.address.line2", address.line2.getOrElse(null)),
      Updates.set("ratepayerRegistration.address.town", address.town),
      Updates.set("ratepayerRegistration.address.county", address.county.getOrElse(null)),
      Updates.set("ratepayerRegistration.address.postcode.value", address.postcode.value)
    )
    findAndUpdateByCredId(credId, updates: _*)
  }

  def updateTRN(credId: CredId, trnReferenceNumber: TRNReferenceNumber): Future[Option[RatepayerRegistrationValuation]] = {
    val updates = Seq(
      Updates.set("ratepayerRegistration.trnReferenceNumber.referenceType", trnReferenceNumber.referenceType.toString),
      Updates.set("ratepayerRegistration.trnReferenceNumber.value", trnReferenceNumber.value)
    )
    findAndUpdateByCredId(credId, updates: _*)
  }

  def updateNino(credId: CredId, nino: Nino): Future[Option[RatepayerRegistrationValuation]] = {
    findAndUpdateByCredId(credId, Updates.set("ratepayerRegistration.nino", nino.value))
  }

  def registerAccount(credId: CredId): Future[Option[RatepayerRegistrationValuation]] = {
    findAndUpdateByCredId(credId, Updates.set("ratepayerRegistration.isRegistered", true))
  }


  def keepAlive(credId: CredId): Future[Boolean] = {
    collection
      .updateOne(
        filter = equal("credId.value", credId.value),
        update = Updates.set("createdAt", Instant.now())
      )
      .toFuture()
      .map(_.wasAcknowledged())
  }

}
