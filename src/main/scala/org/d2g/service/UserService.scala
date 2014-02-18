package org.d2g.service

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.pipe
import org.d2g.activerecord.User

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
case class UpdateUserMessage(user: User)

/**
 * Create user message.
 *
 * @param user DTO object representing new user
 */
case class CreateUserMessage(user: User)

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
			logger.info("GetUserByIdMessage")
			val future = User.findById(id)
			future pipeTo sender

		case GetAllUsersMessage =>
			logger.info("SaveUserMessage")
			val futureUsers = User.findAll()
			futureUsers pipeTo sender

		case CreateUserMessage(user) =>
			logger.info("SaveUserMessage")
			val futureUser = User.insert(user)
			futureUser pipeTo sender

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
