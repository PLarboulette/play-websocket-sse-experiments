package controllers

import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub}
import play.engineio.EngineIOController
import play.socketio.scaladsl.SocketIO


class ChatEngine(socketIO: SocketIO)(implicit mat: Materializer) {

  import play.socketio.scaladsl.SocketIOEventCodec._

  // This will decode String "message" events coming in
  val decoder: SocketIOEventsDecoder[String] = decodeByName {
    case "message" => decodeJson[String]
  }

  // This will encode String "chat message" events going out
  val encoder: SocketIOEventsEncoder[String] = encodeByType[String] {
    case _: String => "message" -> encodeJson[String]
  }

  private val chatFlow = {
    // We use a MergeHub to merge all the incoming chat messages from all the
    // connected users into one flow, and we feed that straight into a
    // BroadcastHub to broadcast them out again to all the connected users.
    // See http://doc.akka.io/docs/akka/snapshot/scala/stream/stream-dynamic.html
    // for details on these features.
    val (sink, source) = MergeHub.source[String]
        .map {
          message =>
            println(message)
            message
        }
      .toMat(BroadcastHub.sink)(Keep.both).run

    // We couple the sink and source together so that one completes, the other
    // will to, and we use this to handle our chat
    Flow.fromSinkAndSourceCoupled(sink, source)
  }

  // Here we create an EngineIOController to handle requests for our chat
  // system, and we add the chat flow under the "/chat" namespace.
  val controller: EngineIOController = socketIO.builder
    .addNamespace("/chat", decoder, encoder, chatFlow)
    .createController()
}