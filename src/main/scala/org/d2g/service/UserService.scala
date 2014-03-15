package org.d2g.service

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.pipe
import org.d2g.activerecord.{ServiceException, User}
import org.d2g.dto.{PublicProfile, PrivateProfile}


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
case class CreateUserMessage(user: PrivateProfile)

/**
 * Delete user message.
 *
 * @param id string BSON id representation of user which should be removed
 */
case class DeleteUserMessage(id: String)

class UserServiceActor extends Actor {

	val logger = Logging(context.system, this)

	implicit val ec = context.dispatcher


	def receive = {
		case GetUserByIdMessage(id) =>
			logger.info(id)
			val future = User.findById(id).map {opt: Option[User] => opt.map(_.publicProfile)}
			future pipeTo sender

		case GetAllUsersMessage =>
			logger.info("SaveUserMessage")
			val futureUsers = User.findAll()
			futureUsers pipeTo sender

		case CreateUserMessage(profile) =>
			logger.info("Going to create user")
			val user = User.create(profile)
			val future = user.save map {
				case Left(exception) =>
					logger.error("Got an error while trying to create user", exception)
					Left(exception)
				case Right(id) =>
					logger.info(s"New user id is: $id")
					Right(id)
			} recover {
				case e:Exception =>
					logger.error("blala {}", e)
					Left(e)
			}
			future pipeTo sender

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
