package org.d2g.controller

import org.scalatra._
import _root_.akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.Future
import _root_.akka.pattern.ask
import org.d2g.service.{AuthenticateUserMessage, GetUserByIdMessage, GetAllUsersMessage, RegisterNewUserMessage}
import org.scalatra.swagger._
import org.d2g.activerecord.ResourceNotFoundException
import org.d2g.dto.{UserLoginCredentials, PrivateProfile, PublicProfile}
import org.d2g.controller.UrlUtils._
import scala.util.{Failure, Success, Try}
import org.d2g.auth.AuthenticationSupport

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
class UserController(userService: ActorRef)(implicit val swagger: Swagger, implicit val system: ActorSystem)
	extends ScalatraServlet with JsonSupport with AkkaSupport with UserSwaggerDefinition with AuthenticationSupport {

	before() {
		contentType = formats("json")
	}

	post("/register") {
		val profile = parsedBody.extract[PrivateProfile]

		//TODO: look to more specific error handling (duplicate email)
		new AsyncResult {
			val is = (userService ? RegisterNewUserMessage(profile)).mapTo[Try[String]].map {
				case Success(id) => Created(headers = Map("Location" -> createUrlForUser(id)))
				case Failure(e) => InternalServerError(e.getMessage)
			}
		}
	}

	post("/login") {
		val credentials = parsedBody.extract[UserLoginCredentials]
		new AsyncResult() {
			val is = (userService ? AuthenticateUserMessage(credentials)).mapTo[Try[String]].map {
				case Success(token) => Ok(token)
				case Failure(cause) => cause match {
					case e: ResourceNotFoundException => Forbidden()
					case _ => InternalServerError()
				}

			}
		}
	}


	delete("/logout") {

	}

	get("/:id", operation(UserProfile)) {
		val visibility = params.getOrElse("profile", "public")
		val id: String = params("id")
		new AsyncResult {
			val is = (userService ? GetUserByIdMessage(id)).mapTo[Option[PublicProfile]].map {
				case None => NotFound()
				case Some(profile) => Ok(profile)
			}
		}
	}

	get("/all", operation(UserList)) {
		val skip: Int = params.getOrElse("skip", "0").toInt
		val count: Int = params.getOrElse("count", "25").toInt
		new AsyncResult {
			val is: Future[List[PublicProfile]] = (userService ? GetAllUsersMessage).mapTo[List[PublicProfile]]
		}
	}

}

trait UserSwaggerDefinition extends SwaggerSupport {

	override protected val applicationName = Some("User")

	protected val applicationDescription = "D2G User API." +
		" This API exposes operations for user management: " +
		" user retrieving, updating, searching, creation, etc."

	val MaxUsersToLoad: Int = 25

	val CountOfUsersToSkip: Int = 0

	val UserList =
		apiOperation[List[PublicProfile]]("getUsersListPage")
			.summary(s"Load list of max $MaxUsersToLoad users skipping first $CountOfUsersToSkip users, ordered by id descending")
			.parameters(
				pathParam[Int]("skip").description("Max count of users to load").optional.defaultValue(MaxUsersToLoad),
				pathParam[Int]("count").description("How many users to skip from start").optional.defaultValue(CountOfUsersToSkip)
			)

	val UserProfile =
		apiOperation[List[PublicProfile]]("getUserById")
			.summary("Get user public profile by specific user id")
			.parameter(pathParam[String]("id").description("User identifier").required)
}