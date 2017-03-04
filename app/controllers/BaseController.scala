package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller

abstract class BaseController extends Controller {

  implicit val defaultDispatcher = defaultContext

}
