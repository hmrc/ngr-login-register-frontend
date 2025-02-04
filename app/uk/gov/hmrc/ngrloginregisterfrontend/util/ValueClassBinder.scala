package uk.gov.hmrc.ngrloginregisterfrontend.util

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads}
import play.api.mvc.PathBindable

object ValueClassBinder {

  def valueClassBinder[A: Reads](fromAtoString: A => String)(implicit stringBinder: PathBindable[String]): PathBindable[A] = {

    def parseString(str: String) =
      JsString(str).validate[A] match {
        case JsSuccess(a, _) => Right(a)
        case JsError(error)  => Left(s"No valid value in path: $str. Error: ${error.toString}")
      }

    new PathBindable[A] {
      override def bind(key: String, value: String): Either[String, A] =
        stringBinder.bind(key, value).flatMap(parseString)

      override def unbind(key: String, a: A): String =
        stringBinder.unbind(key, fromAtoString(a))
    }
  }

}
