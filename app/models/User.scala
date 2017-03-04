package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.libs.json.{Format, Json, OFormat}
import reactivemongo.bson.BSONObjectID
import silhouette.Roles.{Role, UserRole}
import play.modules.reactivemongo.json._

case class User (role: Role = UserRole,
                 _id: Option[BSONObjectID] = None,
                 loginInfo: Option[LoginInfo] = None,
                 firstName: Option[String] = None,
                 lastName: Option[String] = None,
                 email: Option[String] = None,
                 passwordInfo: Option[PasswordInfo] = None ) extends Identity

case class LoginData ( email: String,
                       password: String,
                       rememberMe: Boolean = false)

case class SignUpData( firstName: String,
                       lastName: String,
                       email: String,
                       password: String)

object JsonFormatUser {

  import silhouette.Roles.rolesFormat
  import LoginInfo.jsonFormat

  implicit val passwordInfoFormat: Format[PasswordInfo] = Json.format[PasswordInfo]
  implicit val userFormat: Format[User] = Json.format[User]
  implicit val userOFormat: OFormat[User] = Json.format[User]
  implicit val loginFormat = Json.format[LoginData]
  implicit val signUpFormat = Json.format[SignUpData]



}