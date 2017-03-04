package daos.mongo

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.LoginInfo
import daos.api.UserDao
import models.User
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import models.JsonFormatUser._
import play.modules.reactivemongo.json._

import scala.concurrent.Future

class UserDaoMongoImpl (reactiveMongoApi: ReactiveMongoApi, actorSystem: ActorSystem) extends BaseDaoMongoImpl[User]("users", reactiveMongoApi, actorSystem) with UserDao {

  override def find(loginInfo: LoginInfo): Future[Option[User]] = {
    super.findOne( Json.obj("loginInfo" -> loginInfo) )
  }

}
