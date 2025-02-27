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

import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.PlaySpec
import play.api.http.HeaderNames
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.ngrloginregisterfrontend.util.CentralAuthHelper

class CentralAuthHelperSpec extends PlaySpec with Matchers {

  "extractGNAPTokenFromRH" should {
    "extract out nothing if Authorization header is None && Session is empty" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders()
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header is empty string and Session is empty" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header is None and Session key is empty string" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header just has bearer, and Session key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "Bearer foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header is none, and Session key just has Bearer" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "Bearer foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header just has commas, session is None" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> ", , ,")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out nothing if Authorization header just has random character and Session key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "foo, bar, &^%$2,")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe None
    }
    "extract out GNAP token if just GNAP token exists in Authorization header and Session key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "GNAP foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if just GNAP token exists in Session key and Authorisation key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "GNAP foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if  multiple tokens exist in Authorization header, GNAP order first, Session key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "GNAP foo, Bearer bar")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if  multiple tokens exist in Session key, GNAP order first, Authorization header is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "GNAP foo, Bearer bar")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if multiple tokens exist in Authorization header in GNAP order last, Session key is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "Bearer bar, GNAP foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if multiple tokens exist in Session key in GNAP order last, Authorization header is none" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "Bearer bar, GNAP foo")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
    "extract out GNAP token if both Authorisation header and session key exists with GNAP token, take session first" in {
      implicit val rh: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.authToken -> "Bearer bar, GNAP foo").withHeaders(HeaderNames.AUTHORIZATION -> "GNAP notused, Bearer bar")
      CentralAuthHelper.extractGNAPTokenFromRH mustBe Some("foo")
    }
  }
}
