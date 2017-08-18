package controllers

import java.util.Locale
import javax.inject._

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import com.github.javafaker.Faker
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.mutable
import scala.concurrent.duration._


@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  val list: mutable.Map[Int,Event] = createRandomEvents(50)

  def serverSideEvent() = Action {
    Ok.chunked( stringSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  def stringSource: Source[String, _] = {

    def flow: Flow[mutable.Map[Int, Event], (Int, Event), NotUsed] =
      Flow[mutable.Map[Int,Event]]
        .map {_
          .filter { case (_, event) => !event.alreadySent}
          .maxBy {case (_, event) => event.created}
        }

    Source.tick(0.millis, 1.second, list)
      .via(flow)
      .map{ case(id, event) =>
        list.update(id, event.update(true))
        s"ID : $id --> Beer : ${event.text}"
      }
  }

  def createRandomEvents (count : Int) : mutable.Map[Int, Event] = {

    def createRandomEvent ( index : Int) : Event = {
      val faker = new Faker(new Locale("fr"))
      Event(index.toString, faker.beer().name(), alreadySent = false, index * 100)
    }

    (0 to count).toList.foldLeft(mutable.Map.empty[Int, Event]) {
      (events, index) =>
        events +=((index, createRandomEvent(index)))
    }
  }
}

case class Event (id : String, text : String, alreadySent : Boolean, created : Long) {

  def update (sent : Boolean): Event = this.copy(alreadySent = sent)

}

object Event {
  implicit val reads = Json.reads[Event]
  implicit val writes = Json.writes[Event]
}
