package org.d2g.auth

import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.d2g.activerecord.User
import org.d2g.auth.strategies.UserPasswordStrategy
import org.d2g.controller.{AkkaSupport, JsonSupport}
import org.scalatra.ScalatraBase

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 17/03/2014
 */
trait AuthenticationSupport extends ScentrySupport[User] {
	self: ScalatraBase with JsonSupport with AkkaSupport =>

	protected def fromSession = {
		case id: String => User(None, "e", "a", "d", "d", "f", "g", None, "d", isActive = false, isAdmin = false, None, None, None)
	}

	protected def toSession = {
		case usr: User => "23"
	}

	protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

	protected def requireLogin() = {
		if (!isAuthenticated) {
			redirect(scentryConfig.login)
		}
	}

	/**
	 * If an unauthenticated user attempts to access a route which is protected by Scentry,
	 * run the unauthenticated() method on the UserPasswordStrategy.
	 */
	override protected def configureScentry = {
		scentry.unauthenticated {
			scentry.strategies("UserPassword").unauthenticated()
		}
	}

	/**
	 * Register auth strategies with Scentry. Any controller with this trait mixed in will attempt to
	 * progressively use all registered strategies to log the user in, falling back if necessary.
	 */
	override protected def registerAuthStrategies = {
		scentry.register(new UserPasswordStrategy(self))
	}

}
