package daos.mongo

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import daos.api.{PasswordInfoDao}
import models.User
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import models.JsonFormatUser._
import play.modules.reactivemongo.json._

import scala.concurrent.Future

class PasswordInfoDaoMongoImpl (reactiveMongoApi: ReactiveMongoApi, actorSystem: ActorSystem) extends BaseDaoMongoImpl[User] ("users", reactiveMongoApi, actorSystem) with PasswordInfoDao {

  val delegableDao = new Delegable(this)

  class Delegable ( father: PasswordInfoDaoMongoImpl ) extends DelegableAuthInfoDAO[PasswordInfo] {

    override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
      father.findOne( Json.obj("loginInfo" -> loginInfo) ).map {
        case Some(user) => user.passwordInfo
        case _ => None
      }

    }

    override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {

      father.update( Json.obj("loginInfo" -> loginInfo), Json.obj("passwordInfo" -> authInfo) )
        .map { _ => authInfo }
    }

    override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
      add(loginInfo, authInfo)
    }

    override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
      add(loginInfo, authInfo)
    }

    override def remove(loginInfo: LoginInfo): Future[Unit] = {
      father.db.flatMap {
        _.update( Json.obj("loginInfo" -> loginInfo),
          Json.obj("$unset" -> Json.obj("loginInfo" -> loginInfo)) )
          .map { _ => Unit }
      }
    }
  }


}
