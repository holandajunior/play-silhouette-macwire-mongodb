package daos

import akka.actor.ActorSystem
import daos.api.BaseDao

import scala.concurrent.ExecutionContext

abstract class BaseDaoImpl[T] (actorSystem: ActorSystem) extends BaseDao[T] {

  implicit val dbDispatcher: ExecutionContext = actorSystem.dispatchers.lookup("akka.actor.db-dispatcher")
}
