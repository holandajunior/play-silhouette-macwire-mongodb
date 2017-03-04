package daos.api

import play.api.libs.json.{JsObject, JsValue, OFormat}

import scala.concurrent.Future

trait BaseDao[T] {

  /**
    * Save an entity
    * @param entity
    * @return
    */
  def save(entity: T)(implicit format : OFormat[T]) : Future[T]

  /**
    * Find all
    * @return
    */
  def find(selector: JsObject)(implicit format : OFormat[T]) : Future[Seq[T]]

  /**
    * Find one acc
    * @param format
    * @return
    */
  def findOne(selector: JsObject)(implicit format: OFormat[T]) : Future[Option[T]]

  /**
    * Remove an entity
    * @param entity
    * @return
    */
  def delete(entity: T)(implicit format : OFormat[T]) : Future[T]

  /**
    * Update an entity
    * @param selector A json describing where it must be updated
    * @param entity
    * @return
    */
  def updateByEntity(selector: JsObject, entity: T, upsert: Boolean = true)(implicit format : OFormat[T]) : Future[Option[T]]

  /**
    * Update en entity by using json data
    * @param selector
    * @param toUpdate
    * @param upsert
    * @param format
    * @return
    */
  def update(selector: JsObject, toUpdate: JsValue, upsert: Boolean = true)(implicit format : OFormat[T]) : Future[Option[T]]

}
