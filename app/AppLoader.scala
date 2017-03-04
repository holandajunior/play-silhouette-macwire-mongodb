import akka.actor.ActorSystem
import controllers.Assets
import modules.BuiltInComponentModule
import play.api._
import play.api.ApplicationLoader.Context
import play.api.libs.concurrent.Akka
import play.api.routing.Router
import router.Routes

class AppLoader extends ApplicationLoader{

  def load(context: Context): Application = {

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppComponents extends BuiltInComponentModule {

  import com.softwaremill.macwire.wire

  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}
