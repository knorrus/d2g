package org.d2g.service

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.pipe
import org.d2g.activerecord.{InconsistentResourceException, ResourceNotFoundException, User}
import org.d2g.dto.{UserLoginCredentials, PrivateProfile}
import scala.util.{Failure, Success}
import org.d2g.connector.Redis

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */

/**
 * Get specific user message.
 *
 * @param id String BSON id representation of user which should be removed
 */
case class GetUserByIdMessage(id: String)

/**
 * Load all users message.
 */
case class GetAllUsersMessage()

/**
 * Update user message.
 *
 * @param user DTO object representing new state of existing user
 */
case class UpdateUserMessage(user: PrivateProfile)

/**
 * Create user message.
 *
 * @param user DTO object representing new user
 */
case class RegisterNewUserMessage(user: PrivateProfile)

case class AuthenticateUserMessage(credentials: UserLoginCredentials)

case class GetUserByEmailMessage(email: String)

/**
 * Delete user message.
 *
 * @param id string BSON id representation of user which should be removed
 */
case class DeleteUserMessage(id: String)

class UserServiceActor extends Actor {

	import org.d2g.utils.SecurityUtils._

	val logger = Logging(context.system, this)

	implicit val ec = context.dispatcher

	def receive = {
		case RegisterNewUserMessage(profile) =>
			logger.info("D2G: register new user")
			val user = User.create(profile)
			val future = user.save map {
				case Left(exception) =>
					logger.error("Got an error while trying to create user", exception)
					Left(exception)
				case Right(id) =>
					logger.info(s"New user id is: $id")
					Right(id)
			} recover {
				case e: Exception =>
					logger.error("blala {}", e)
					Left(e)
			}
			future pipeTo sender

		case AuthenticateUserMessage(credentials) =>
			val future = User.getByEmail(credentials.email) map {
				opt: Option[User] =>
					val user = opt.getOrElse(throw InconsistentResourceException(List(credentials.email)))
					val passwordHash = sha256(sha256(credentials.password).concat(user.salt))
					if (passwordHash != user.passwordHash) {
						throw InconsistentResourceException(List(credentials.password))
					}

					Redis.instance.exec { connection =>

					}
					Success("OK")
			} recover {
				case e: Exception => Failure(e)
			}
			future pipeTo sender

		case GetUserByEmailMessage(email) =>
			logger.info(email)
			val future = User.getByEmail(email)
			future pipeTo sender

		case GetUserByIdMessage(id) =>
			logger.info(id)
			val future = User.findById(id).map {
				opt: Option[User] => opt.map(_.publicProfile)
			}
			future pipeTo sender

		case GetAllUsersMessage =>
			logger.info("SaveUserMessage")
			val futureUsers = User.findAll()
			futureUsers pipeTo sender

		case UpdateUserMessage(user) =>
			logger.info("UpdateUserMessage")
			sender ! "Not implemented yet"

		case DeleteUserMessage(id) =>
			logger.info("DeleteUserMessage")
			val futureUsers = User.remove(id)
			futureUsers pipeTo sender
		case _ =>
			logger.info("received unknown message")
	}
}
