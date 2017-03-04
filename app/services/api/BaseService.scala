package services.api

import scala.concurrent.Future

trait BaseService[T] {

  /**
    * Save an entity
    * @param entity
    * @return
    */
  def save(entity: T) : Future[T]

  /**
    * Find all
    * @return
    */
  def find : Future[Seq[T]]
}
