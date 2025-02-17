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

package uk.gov.hmrc.ngrloginregisterfrontend.utils
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.util.AffinityGroupHelper

class AffinityGroupHelperSpec extends TestSupport {
  val individual: AffinityGroup = AffinityGroup.Individual
  val optionalIndividual: Option[AffinityGroup] = Some(AffinityGroup.Individual)
  val organisation: AffinityGroup = AffinityGroup.Organisation
  val optionalOrganisation: Option[AffinityGroup] = Some(AffinityGroup.Organisation)
  val agent: AffinityGroup = AffinityGroup.Agent
  val optionalAgent: Option[AffinityGroup] = Some(AffinityGroup.Agent)


  "AffinityGroupHelper" must {


    "return true when isIndividual" in {
      AffinityGroupHelper.isIndividual(individual) mustBe true
    }
    "return false when isIndividual is agent" in {
      AffinityGroupHelper.isIndividual(agent) mustBe false
    }
    "return false when isIndividual is organisation" in {
      AffinityGroupHelper.isIndividual(organisation) mustBe false
    }
    "return true when optional individual" in {
      AffinityGroupHelper.isIndividual(optionalIndividual) mustBe true
    }
    "return false when isIndividual is none" in {
      AffinityGroupHelper.isIndividual(None) mustBe false
    }

    "return true when isOrganisation" in {
      AffinityGroupHelper.isOrganisation(organisation) mustBe true
    }
    "return false when isOrganisation is agent" in {
      AffinityGroupHelper.isOrganisation(agent) mustBe false
    }
    "return false when isOrganisation is individual" in {
      AffinityGroupHelper.isOrganisation(individual) mustBe false
    }
    "return true when optional organisation" in {
      AffinityGroupHelper.isOrganisation(optionalOrganisation) mustBe true
    }
    "return false when isOrganisation is none" in {
      AffinityGroupHelper.isOrganisation(None) mustBe false
    }

    "return true when isAgent" in {
      AffinityGroupHelper.isAgent(agent) mustBe true
    }
    "return false when isAgent is individual" in {
      AffinityGroupHelper.isAgent(individual) mustBe false
    }
    "return false when isAgent is organisation" in {
      AffinityGroupHelper.isAgent(organisation) mustBe false
    }
    "return true when optional agent" in {
      AffinityGroupHelper.isAgent(optionalAgent) mustBe true
    }
    "return false when isAgent is none" in {
      AffinityGroupHelper.isAgent(None) mustBe false
    }


  }

}
