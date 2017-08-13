package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject._

import akka.stream.scaladsl.Source
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.mvc._

import scala.concurrent.duration._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def serverSideEvent() = Action {
    Ok.chunked( stringSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  def stringSource: Source[String, _] = {
    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("HH mm ss")
    Source.tick(0.millis, 1.second, "TICK")
      .map( tick => df.format(ZonedDateTime.now()))
  }



}
