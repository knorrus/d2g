package org.d2g.service

import akka.actor.Actor
import org.d2g.domain.User
import akka.event.Logging
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext
import reactivemongo.bson.BSONDocument

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */

/**
 *
 * @param id
 */
case class GetUserByIdMessage(id: Long)

/**
 *
 */
case class GetAllUsersMessage()

/**
 *
 * @param user
 */
case class UpdateUserMessage(user: User)

/**
 *
 * @param user
 */
case class SaveUserMessage(user: User)

/**
 *
 * @param id
 */
case class DeleteUserMessage(id: Long)

/**
 *
 * @param db
 */
class UserServiceActor(db: DefaultDB) extends Actor {

	val logger = Logging(context.system, this)

	implicit val executionContext: ExecutionContext = context.dispatcher

	implicit val collection = db[BSONCollection]("users")

	def receive = {
		/*case GetUserByIdMessage(id) =>
			logger.info("GetUserByIdMessage")
			sender ! getUserById(id)*/
		case GetAllUsersMessage =>
			logger.info("GetAllUsersMessage")
			sender ! List(User(None, "vasya", "telka"))
			collection.find(BSONDocument()).cursor[User].collect[List]().map(users => sender ! List(User(None, "vasya", "telka")))
		case _ =>
			logger.info("received unknown message")
			sender ! List()

		/*		case UpdateUserMessage(user) =>
					logger.info("UpdateUserMessage")
					sender ! updateUser(user)
				case SaveUserMessage(user) =>
					logger.info("SaveUserMessage")
					sender ! saveUser(user)
				case DeleteUserMessage(id) =>
					logger.info("DeleteUserMessage")
					sender ! deleteUser(id)*/
	}
}
