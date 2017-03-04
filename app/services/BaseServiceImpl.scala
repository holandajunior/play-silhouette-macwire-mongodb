package services

import akka.actor.ActorSystem
import services.api.BaseService

import scala.concurrent.ExecutionContext

abstract class BaseServiceImpl[T] (actorSystem: ActorSystem) extends BaseService[T] {

  implicit val serviceDispatcher: ExecutionContext = actorSystem.dispatchers.lookup("akka.actor.service-dispatcher")

}
