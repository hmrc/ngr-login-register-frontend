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

import com.mongodb.client.model.Indexes.descending
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, ReplaceOptions}
import play.api.Logging
import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.ngrloginregisterfrontend.config.FrontendAppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.{ErrorResponse, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{LookUpAddresses, LookedUpAddress}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
case class  NgrFindAddressRepo @Inject()(mongo: MongoComponent,
                                         config: FrontendAppConfig
                                        )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[LookUpAddresses](
    collectionName = "addressLookup",
    mongoComponent = mongo,
    domainFormat = LookUpAddresses.format,
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

  def upsertLookupAddresses(lookUpAddresses: LookUpAddresses): Future[Boolean] = {
    val errorMsg = s"Addresses have not been inserted"

    collection.replaceOne(
      filter = equal("credId.value", lookUpAddresses.credId.value),
      replacement = lookUpAddresses,
      options = ReplaceOptions().upsert(true)
    ).toFuture().transformWith {
      case Success(result) =>
        logger.info(s"RatepayerRegistration has been upsert for credId: ${lookUpAddresses.credId.value}")
        result.wasAcknowledged()
        Future.successful(true)
      case Failure(exception) =>
        logger.error(errorMsg)
        Future.failed(new IllegalStateException(s"$errorMsg: ${exception.getMessage} ${exception.getCause}"))
    }
  }

  def findByCredId(credId: CredId): Future[LookUpAddresses] = {
    collection.find(
      equal("credId.value", credId.value)
    ).headOption().map(addresses => addresses.getOrElse(LookUpAddresses(credId = credId, postcode = Postcode(""))))
  }

}
