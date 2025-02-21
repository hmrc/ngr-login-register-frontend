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

class EqualitySpec extends TestSupport {

  "Eq" should {

    case class Person(name: String, age: Int)

   "return true for equal integers" in {
      val intEq: Eq[Int] = Eq.fromUniversalEquals[Int]
      assert(intEq.eqv(5, 5))
    }

    "return false for unequal integers" in {
      val intEq: Eq[Int] = Eq.fromUniversalEquals[Int]
      assert(!intEq.eqv(5, 10))
    }

    "return true for equal strings" in {
      val stringEq: Eq[String] = Eq.fromUniversalEquals[String]
      assert(stringEq.eqv("hello", "hello"))
    }

    "return false for unequal strings" in {
      val stringEq: Eq[String] = Eq.fromUniversalEquals[String]
      assert(!stringEq.eqv("hello", "world"))
    }

   "return true for equal case class instances" in {
      val personEq: Eq[Person] = Eq.fromUniversalEquals[Person]
      val person1 = Person("John", 30)
      val person2 = Person("John", 30)
      assert(personEq.eqv(person1, person2))
    }

     "return false for unequal case class instances" in {
      val personEq: Eq[Person] = Eq.fromUniversalEquals[Person]
      val person1 = Person("John", 30)
      val person2 = Person("Jane", 25)
      assert(!personEq.eqv(person1, person2))
    }

    "return true for the same object instance" in {
      val stringEq: Eq[String] = Eq.fromUniversalEquals[String]
      val s = "hello"
      assert(stringEq.eqv(s, s))
    }
  }
}