package silhouette

import com.mohiva.play.silhouette.api.Authorization
import models.User
import play.api.libs.json._
import play.api.mvc.Request
import silhouette.Roles.Role
import silhouette.environments.DefaultEnv
import play.api.libs.functional.syntax._

import scala.concurrent.Future

object Roles {

  sealed abstract class Role(val name: String)
  case object AdminRole extends Role("admin")
  case object UserRole extends Role("user")

  val roles = Seq[Role](AdminRole, UserRole)

  /* JSON Implicits */
  val roleReads: Reads[Role] = (__ \ "name").read[String].map { s =>
    roles.find( role => role.name.equals(s) ).getOrElse(UserRole)
  }

  val roleWrites : Writes[Role] = (__ \ "name").write[String].contramap { ( role: Role ) =>
    role.name
  }

  implicit val rolesFormat = Format(roleReads, roleWrites)
}


case class WithRole(role: Role) extends Authorization[User, DefaultEnv#A] {

  override def isAuthorized[B](user: User, authenticator: DefaultEnv#A)(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(user.role == role)
  }
}