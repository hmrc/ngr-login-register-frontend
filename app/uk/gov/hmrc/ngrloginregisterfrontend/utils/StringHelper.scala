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

import org.apache.commons.lang3.StringUtils

trait StringHelper {
  def maskNino(nino: String): String = {
    maskString(nino.replaceAll(" ", ""), 3)
  }

  def maskSAUTR(sautr: String): String = {
    maskString(sautr, 3)
  }

  def maskString(input: String, unmaskCharsNumber: Int): String = {
    StringUtils.overlay(input, StringUtils.repeat("*", input.length - unmaskCharsNumber), 0, input.length - unmaskCharsNumber)
  }
}
