package org.d2g.controller

import org.scalatra.{Accepted, AsyncResult, FutureSupport, ScalatraServlet}
import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import akka.util.Timeout
import org.d2g.service.GetAllUsersMessage

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
class UserController(system: ActorSystem, userService: ActorRef) extends ScalatraServlet with JsonSupport with FutureSupport {

	protected implicit def executor: ExecutionContext = system.dispatcher

	implicit val defaultTimeout = Timeout(10 seconds)

	before() {
		contentType = formats("json")
	}

	get("/users/:id") {
		new AsyncResult {
			val is = userService ? GetAllUsersMessage
		}
	}

	post("/users") {
		Accepted()
	}

	put("/users/:id") {
		// update the article which has the specified :id
	}

	delete("/users/:id") {
		// delete the article with the specified :id
	}

}