package daos.api

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

trait UserDao extends BaseDao[User] {

  /** Finds a user by its login info.
    *
    *  @param loginInfo The login info of the user to find.
    *  @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]]

}
