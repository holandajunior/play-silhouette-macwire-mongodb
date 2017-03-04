package services

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.LoginInfo
import daos.api.UserDao
import models.User
import services.api.UserService
import models.JsonFormatUser._
import play.api.libs.json.Json

import scala.concurrent.Future

class UserServiceImpl (actorSystem: ActorSystem, userDao: UserDao) extends BaseServiceImpl[User] (actorSystem) with UserService {

  override def save(user: User): Future[User] = {
    userDao.save(user)
  }

  override def retrieve(loginInfo: LoginInfo) : Future[Option[User]] = {
    userDao.find(loginInfo)
  }

  override def find : Future[Seq[User]] = {
    userDao.find(Json.obj())
  }
}
