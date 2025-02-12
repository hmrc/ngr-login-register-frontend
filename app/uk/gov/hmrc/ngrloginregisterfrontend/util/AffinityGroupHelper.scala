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

package uk.gov.hmrc.ngrloginregisterfrontend.util
import uk.gov.hmrc.auth.core.AffinityGroup

object AffinityGroupHelper {
  def isIndividual(ag: Option[AffinityGroup]): Boolean =
    ag match {
      case Some(value) => isIndividual(value)
      case _ => false
    }
  def isIndividual(ag: AffinityGroup): Boolean =
    ag match {
      case AffinityGroup.Agent => false
      case AffinityGroup.Individual => true
      case AffinityGroup.Organisation => false
    }

  def isOrganisation(ag: Option[AffinityGroup]): Boolean =
    ag match {
      case Some(value) => isOrganisation(value)
      case _ => false
    }
  def isOrganisation(ag: AffinityGroup): Boolean =
    ag match {
      case AffinityGroup.Agent => false
      case AffinityGroup.Individual => false
      case AffinityGroup.Organisation => true
    }

  def isAgent(ag: Option[AffinityGroup]): Boolean =
    ag match {
      case Some(value) => isAgent(value)
      case _ => false
    }
  def isAgent(ag: AffinityGroup): Boolean =
    ag match {
      case AffinityGroup.Agent => true
      case AffinityGroup.Individual => false
      case AffinityGroup.Organisation => false
    }
}
