package org.d2g.controller

import org.scalatra._
import scala.concurrent.duration._
import _root_.akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.{Future, ExecutionContext}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import org.d2g.service.{GetUserByIdMessage, GetAllUsersMessage, SaveUserMessage}
import reactivemongo.core.commands.LastError
import org.d2g.activerecord.User

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

	get("/users") {
		new AsyncResult {
			val is = userService ? GetAllUsersMessage
		}
	}

	get("/users/:id") {
		new AsyncResult {
			val is = userService ? GetUserByIdMessage
		}
	}

	post("/users") {
		val user = parsedBody.extract[User]
		val message = SaveUserMessage(user)
		new AsyncResult {
			val is: Future[User] = (userService ? message).mapTo[User]

		}

	}

	put("/users/:id") {
		NotImplemented()
	}

	delete("/users/:id") {
		NotImplemented()
	}

}