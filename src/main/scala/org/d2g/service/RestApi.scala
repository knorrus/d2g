package org.d2g.service

import akka.actor.Actor
import spray.routing._
import org.d2g.model.Product

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class RestApiActor extends Actor with RestApi {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait RestApi extends JsonService {

  val myRoute: Route = path("product" / IntNumber) {
    id =>
      get {
        complete {
          val product = Product(id = Some(id), firstName = "Vasya", lastName = "Pupkin")
          product
        }
      } ~
        put {
          complete {
            "Received PUT request for order " + id
          }
        } ~
        post {
          complete {
            "Recieved POST request for order " + id
          }
        } ~
        delete {
          complete {
            "Recieved DELETE request for oreder " + id
          }
        }
  }
}