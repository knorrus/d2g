package org.d2g

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import org.d2g.service.RestApiActor

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("d2g")

  // create and start our service actor
  val service = system.actorOf(Props[RestApiActor], "api")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8088)
}