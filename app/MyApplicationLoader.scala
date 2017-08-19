
import com.softwaremill.macwire._
import controllers.{AssetsComponents, ChatEngine, HomeController}
import play.api.inject.DefaultApplicationLifecycle
import play.api.mvc.AbstractController
import play.api.{ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext, LoggerConfigurator}
import play.engineio.EngineIOController
import play.socketio.scaladsl.SocketIOComponents

class MyApplicationLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context) =
    new BuiltInComponentsFromContext(context) with MyApplication {
      LoggerConfigurator.apply(context.environment.classLoader)
        .foreach(_.configure(context.environment))
    }.application
}

trait MyApplication extends BuiltInComponents
  with AssetsComponents
  with SocketIOComponents {

  override def applicationLifecycle: DefaultApplicationLifecycle

 /* lazy val chatEngineComplex: ChatEngineComplex = wire[ChatEngineComplex]
  lazy val chatEngineComplexController: EngineIOController = chatEngineComplex.controller*/

  lazy val chatEngine: ChatEngine = wire[ChatEngine]
  lazy val chatEngineController: EngineIOController = chatEngine.controller

  lazy val home: HomeController = wire[HomeController]
  lazy val homeController: AbstractController = home

  override lazy val router = {
    val prefix = "/"
    wire[_root_.router.Routes]
  }
  override lazy val httpFilters = Nil
}