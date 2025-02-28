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

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

class NGRLoggerSpec extends TestSupport {
  val logger: NGRLogger = inject[NGRLogger]
  // todo: Figure out how to check logs
  "NGRLogger" must {
    logger.debug("")
    logger.debug("", new RuntimeException(""))
    logger.info("")
    logger.info("", new RuntimeException(""))
    logger.warn("")
    logger.warn("", new RuntimeException(""))
    logger.error("")
    logger.error("", new RuntimeException(""))
  }
}