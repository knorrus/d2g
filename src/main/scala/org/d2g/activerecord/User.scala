package org.d2g.activerecord

import reactivemongo.bson._
import com.github.nscala_time.time.TypeImports.DateTime
import org.d2g.activerecord.manager.{MongoEntityManager, UserEntityManager}
import scala.concurrent.Future
import org.d2g.dto.{PublicProfile, PrivateProfile}
import org.d2g.utils.SecurityUtils

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

	def save: Future[Either[ServiceException, String]] = entityManager.insert(this)

	def publicProfile = {
		PublicProfile(
			id = this._id map {
				bsonId =>  bsonId.stringify
			},
			login = this.username,
			firstName = this.firstName,
			lastName = this.lastName,
			email = this.email,
			avatar = this.avatarUrl
		)
	}


	def privateProfile = {
		PrivateProfile(
			id = this._id map {
				bsonId => bsonId.stringify
			},
			login = this.username,
			firstName = this.firstName,
			lastName = this.lastName,
			email = this.email,
			avatar = this.avatarUrl,
			password = None,
			location = this.location,
			registeredOn = this.createdDateTime map (date => date.getMillis)
		)
	}

	override def toString: String = s"User(id: ${_id}, name: $username)"
}

object User extends UserEntityManager {}