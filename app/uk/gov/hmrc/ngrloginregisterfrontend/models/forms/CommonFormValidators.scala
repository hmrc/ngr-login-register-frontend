/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.ngrloginregisterfrontend.models.forms

import com.google.common.base.Strings

import java.util.regex.Pattern

trait CommonFormValidators {

  private val fullNameRegexPattern: Pattern     = Pattern.compile("^[A-Za-z .'-]{1,160}$")
  private val phoneNumberRegexPattern: Pattern  = Pattern.compile("^(\\+)?[0-9\\(\\)\\- ]{9,16}$")
  private val postcodeRegexPattern: Pattern = Pattern.compile("[A-Za-z]{1,2}[0-9Rr][0-9A-Za-z]? [0-9][ABD-HJLNP-UW-Zabd-hjlnp-uw-z]{2}")
  private val emailPattern: Pattern =
    Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")

  private val isMatchingPattern: (String, Pattern) => Boolean = (value, pattern) => pattern.matcher(value).matches()

  val isNonEmpty: String => Boolean = value => !Strings.isNullOrEmpty(value) && value.trim.nonEmpty
  val isValidFullName: String => Boolean = (value: String) =>
    value.isEmpty || isMatchingPattern(value, fullNameRegexPattern)
  val isValidTelephoneNumber: String => Boolean = (value: String) =>
    value.isEmpty || isMatchingPattern(value, phoneNumberRegexPattern)
  val isValidEmail: String => Boolean = (email: String) => email.isEmpty || isMatchingPattern(email, emailPattern)
  val isValidPostcode: String => Boolean = (value: String) =>
    value.isEmpty || isMatchingPattern(value, postcodeRegexPattern)
}
