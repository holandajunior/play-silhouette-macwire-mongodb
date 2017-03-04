package daos.mongo

import akka.actor.ActorSystem
import daos.BaseDaoImpl
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection
import play.api.libs.json._
import reactivemongo.api.ReadPreference
import play.modules.reactivemongo.json._

import scala.concurrent.Future

abstract class BaseDaoMongoImpl[T](collection: String, reactiveMongoApi: ReactiveMongoApi, actorSystem: ActorSystem) extends BaseDaoImpl[T] (actorSystem) {

  protected val db = reactiveMongoApi.database.map( db => db[JSONCollection](collection) )

  override def save(entity: T)(implicit format : OFormat[T]): Future[T] = {
    db.flatMap {
      _.insert(entity).map( _ => entity )
    }
  }

  override def find(selector: JsObject)(implicit format : OFormat[T]): Future[Seq[T]] = {
    db.flatMap {
      _.find(selector).cursor[T](ReadPreference.Primary).collect[List]()
    }
  }

  override def findOne(selector: JsObject)(implicit format : OFormat[T]): Future[Option[T]] = {
    db.flatMap {
      _.find(selector).one[T]
    }
  }

  override def delete(entity: T)(implicit format : OFormat[T]): Future[T] = {
    db.flatMap {
      _.remove(entity).map ( _ => entity)
    }
  }

  override def updateByEntity(selector: JsObject, entity: T, upsert: Boolean = false)(implicit format : OFormat[T]): Future[Option[T]] = {
    update(selector, Json.toJson(entity))
  }

  override def update(selector: JsObject, toUpdate: JsValue, upsert: Boolean = false)(implicit format : OFormat[T]): Future[Option[T]] = {
    db.flatMap {
      _.findAndUpdate(selector, Json.obj("$set" -> toUpdate), fetchNewObject = true, upsert = upsert).map { r =>
        r.result[T]
        
      }
    }
  }
}
