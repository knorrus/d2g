package org.d2g.activerecord

import reactivemongo.bson._
import reactivemongo.bson.BSONDateTime
import com.github.nscala_time.time.StaticForwarderImports.DateTime
import com.github.nscala_time.time.TypeImports.DateTime
import org.d2g.activerecord.manager.{MongoEntityManager, UserEntityManager}
import scala.concurrent.Future

case class User(
								 _id: Option[BSONObjectID] = None,
								 username: String,
								 email: String,
								 passwordHash: String,
								 salt: String,
								 firstName: String,
								 lastName: String,
								 avatarUrl: Option[String] = None,
								 location: String,
								 isActive: Boolean = false,
								 isAdmin: Boolean = false,
								 createdDateTime: Option[DateTime] = None,
								 lastUpdatedDateTime: Option[DateTime] = None,
								 lastLoginDateTime: Option[DateTime] = None) extends IdentifiableRecord with ActiveRecord[User] {

	def entityManager: MongoEntityManager[User] = User

	def delete: Future[Either[ServiceException, Boolean]] = entityManager.remove(this._id.getOrElse(throw new UnexpectedServiceException("Trying to remove not persisted entity")))

	def save: Future[Either[ServiceException, User]] = entityManager.insert(this)

	override def toString: String = "User(id: $_id, name: $name)"
}

object User extends UserEntityManager {

}