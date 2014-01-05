package org.d2g.service

import akka.actor.Actor
import org.d2g.domain.User
import akka.event.Logging

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
case class GetUserByIdMessage(id: Long)

case class GetAllUsersMessage()

case class UpdateUserMessage(user: User)

case class SaveUserMessage(user: User)

case class DeleteUserMessage(id: Long)

trait UserService {

	def getUserById(id: Long): User = {
		User("vasya", "123456")
	}

	def getAllUsers: List[User] = {
		val first = User("vasya", "123456")
		val second = User("iva", "654321")
		List(first, second)
	}

	def updateUser(user: User): User = {
		user
	}

	def saveUser(user: User): User = {
		user
	}

	def deleteUser(id: Long): Unit = {

	}
}

class UserServiceActor extends Actor with UserService {

	val logger = Logging(context.system, this)

	def receive = {
		case GetUserByIdMessage(id) =>
			logger.info("GetUserByIdMessage")
			sender ! getUserById(id)
		case GetAllUsersMessage =>
			logger.info("GetAllUsersMessage")
			sender ! getAllUsers
		case UpdateUserMessage(user) =>
			logger.info("UpdateUserMessage")
			sender ! updateUser(user)
		case SaveUserMessage(user) =>
			logger.info("SaveUserMessage")
			sender ! saveUser(user)
		case DeleteUserMessage(id) =>
			logger.info("DeleteUserMessage")
			sender ! deleteUser(id)
	}
}
