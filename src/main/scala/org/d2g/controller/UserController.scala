package org.d2g.controller

import org.scalatra._
import scala.concurrent.duration._
import _root_.akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.{Future, ExecutionContext}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import org.d2g.service.{GetUserByIdMessage, GetAllUsersMessage, CreateUserMessage}
import org.scalatra.swagger._
import org.d2g.activerecord.{ServiceException, User}
import org.json4s.{Formats, DefaultFormats}
import org.scalatra.json.NativeJsonSupport
import org.d2g.dto.{UserLoginCredentials, PrivateProfile, PublicProfile}
import org.d2g.controller.UrlUtils._

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
trait UserSwaggerDefinition extends SwaggerSupport {

	override protected val applicationName = Some("User")

	protected val applicationDescription = "D2G User API." +
		" This API exposes operations for user management: " +
		" user retrieving, updating, searching, creation, etc."

	val MaxUsersToLoad: Int = 25

	val CountOfUsersToSkip: Int = 0

	val getUsersPublicProfiles =
		apiOperation[List[PublicProfile]]("getUsersListPage")
			.summary(s"Load list of max $MaxUsersToLoad users skipping first $CountOfUsersToSkip users, ordered by id descending")
			.parameters(
				pathParam[Int]("skip").description("Max count of users to load").optional.defaultValue(MaxUsersToLoad),
				pathParam[Int]("count").description("How many users to skip from start").optional.defaultValue(CountOfUsersToSkip)
			)

	val getUserPublicProfile =
		apiOperation[List[PublicProfile]]("getUserById")
			.summary("Get user public profile by specific user id")
			.parameter(pathParam[String]("id").description("User identifier").required)
}

class UserController(userService: ActorRef)(implicit val swagger: Swagger, implicit val system: ActorSystem)
	extends ScalatraServlet with UserSwaggerDefinition with NativeJsonSupport with FutureSupport {

	implicit override val jsonFormats: Formats = DefaultFormats

	protected implicit def executor: ExecutionContext = system.dispatcher

	protected implicit val defaultTimeout = Timeout(10 seconds)

	before() {
		contentType = formats("json")
	}

	post("/login") {
		val credentials = parsedBody.extract[UserLoginCredentials]
	}


	delete("/logout") {

	}

	post("/register") {
		val profile = parsedBody.extract[PrivateProfile]
		new AsyncResult {
			val is = (userService ? CreateUserMessage(profile)).mapTo[Either[ServiceException, String]].map {
				case Left(error) => InternalServerError(error.message)
				case Right(id) => Created(headers = Map("Location" -> createUriForUser(id)))
			}
		}
	}

	get("/:id", operation(getUserPublicProfile)) {
		val visibility = params.getOrElse("profile", "public")
		val id: String = params("id")
		new AsyncResult {
			val is  = (userService ? GetUserByIdMessage(id)).mapTo[Option[PublicProfile]].map {
				case None => NotFound()
				case Some(profile) => Ok(profile)
			}
		}
	}

	get("/users", operation(getUsersPublicProfiles)) {
		val skip: Int = params.getOrElse("skip", "0").toInt
		val count: Int = params.getOrElse("count", "25").toInt
		new AsyncResult {
			val is: Future[List[PublicProfile]] = (userService ? GetAllUsersMessage).mapTo[List[PublicProfile]]
		}
	}

	post("/users") {
		val profile = parsedBody.extract[PrivateProfile]
		val message = CreateUserMessage(profile)
		new AsyncResult {
			val is: Future[User] = (userService ? message).mapTo[User]
		}
	}

}