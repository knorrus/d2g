package org.d2g.controller

import org.scalatra._
import scala.concurrent.duration._
import _root_.akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.{Future, ExecutionContext}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import org.d2g.service.{GetUserByIdMessage, GetAllUsersMessage, CreateUserMessage}
import org.scalatra.swagger._
import org.d2g.activerecord.User
import org.json4s.{Formats, DefaultFormats}
import org.scalatra.json.NativeJsonSupport
import org.d2g.dto.PublicProfileDTO

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
class UserController(userService: ActorRef)(implicit val swagger: Swagger, implicit val system: ActorSystem)
	extends ScalatraServlet with SwaggerSupport with NativeJsonSupport with FutureSupport {

	implicit override val jsonFormats: Formats = DefaultFormats

	protected implicit def executor: ExecutionContext = system.dispatcher

	protected implicit val defaultTimeout = Timeout(10 seconds)

	override protected val applicationName = Some("User")

	protected val applicationDescription = "The User API." +
		" It exposes operations for browsing and searching lists " +
		" of users, and retrieving single user."

	before() {
		contentType = formats("json")
	}

	val getUsers = (apiOperation[List[PublicProfileDTO]]("getUsersList")
		summary "Load all users"
		notes "Shows all the flowers in the flower shop. You can search it too.")

	get("/users", operation(getUsers)) {
		new AsyncResult {
			val is: Future[List[PublicProfileDTO]] = (userService ? GetAllUsersMessage).mapTo[List[PublicProfileDTO]]
		}
	}

	get("/users/:id") {
		new AsyncResult {
			val is: Future[PublicProfileDTO] = (userService ? GetUserByIdMessage).mapTo[PublicProfileDTO]
		}
	}

	post("/users") {
		val user = parsedBody.extract[User]
		val message = CreateUserMessage(user)
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