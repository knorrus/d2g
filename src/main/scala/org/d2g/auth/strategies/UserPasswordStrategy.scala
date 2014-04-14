package org.d2g.auth.strategies

import org.scalatra.{Forbidden, ScalatraBase}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.scalatra.auth.ScentryStrategy
import org.d2g.activerecord.User
import org.d2g.controller.{AkkaSupport, JsonSupport}
import org.d2g.dto.UserLoginCredentials
import _root_.akka.pattern.ask
import org.d2g.service.GetUserByEmailMessage
import scala.concurrent.Await
import scala.concurrent.duration._
import org.json4s.Formats

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 17/03/2014
 */
class UserPasswordStrategy(protected val app: ScalatraBase with JsonSupport with AkkaSupport) extends ScentryStrategy[User] {

	override def name: String = "UserPassword"

	/**
	 * Determine whether the strategy should be run for the current request.
	 */
	override def isValid(implicit request: HttpServletRequest): Boolean = authInfo.isDefined

	/**
	 * Extract `UserLoginCredentials` info from request body
	 */
	def authInfo = {
		import app._
		app.parsedBody.extractOpt[UserLoginCredentials]
	}

	def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
		val userService = app.system.actorFor("userRouter")
		val userEmail = authInfo.get.email
		val future = (userService ? GetUserByEmailMessage(userEmail)).mapTo[Option[User]]
		Await.result(future, 3 seconds)
	}

	/** ``
		* What should happen if the user is currently not authenticated?
		*/
	override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
		app.halt(Forbidden())
	}

}
