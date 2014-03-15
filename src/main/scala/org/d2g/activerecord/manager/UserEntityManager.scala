package org.d2g.activerecord.manager

import scala.concurrent.Future
import reactivemongo.api.indexes.IndexType.Ascending
import org.d2g.activerecord.{ResourceWithoutIdException, User}
import reactivemongo.bson._
import org.joda.time.DateTime
import reactivemongo.bson.BSONDateTime
import scala.Some
import reactivemongo.api.collections.default.BSONCollection

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 09/01/2014
 */
trait UserEntityManager extends MongoEntityManager[User] {

	implicit val reader: BSONDocumentReader[User] = UserReader

	implicit val writer: BSONDocumentWriter[User] = UserWriter

	override protected def collection: BSONCollection = {
		db[BSONCollection]("users")
	}

	override protected def ensureIndexes: Future[List[Boolean]] = {
		Future.sequence(List(
			ensureIndex(List("email" -> Ascending), unique = true)
		))
	}

	object UserReader extends BSONDocumentReader[User] {
		def read(doc: BSONDocument): User = {
			val id = doc.getAs[BSONObjectID]("_id")
			val username = doc.getAs[String]("username").get
			val email = doc.getAs[String]("email").get
			val password = doc.getAs[String]("passwordHash").get
			val salt = doc.getAs[String]("salt").get
			val firstName = doc.getAs[String]("firstName").get
			val lastName = doc.getAs[String]("lastName").get
			val avatarUrl = doc.getAs[String]("avatarUrl")
			val location = doc.getAs[String]("location").get
			val created = doc.getAs[BSONDateTime]("createdDateTime").map(dt => new DateTime(dt.value))
			val lastUpdated = doc.getAs[BSONDateTime]("lastUpdatedDateTime").map(dt => new DateTime(dt.value))
			val lastLogin = doc.getAs[BSONDateTime]("lastLoginDateTime").map(dt => new DateTime(dt.value))
			val isActive = doc.getAs[Boolean]("isActive").get
			val isAdmin = doc.getAs[Boolean]("isAdmin").get
			User(id, username, email, password, salt, firstName, lastName, avatarUrl, location, isActive, isAdmin, created, lastUpdated, lastLogin)
		}
	}

	object UserWriter extends BSONDocumentWriter[User] {

		def write(doc: User): BSONDocument = {
			BSONDocument(
				"_id" -> doc._id.getOrElse(throw new ResourceWithoutIdException("User doesn't have Id field specified")),
				"username" -> doc.username,
				"email" -> doc.email,
				"passwordHash" -> doc.passwordHash,
				"salt" -> doc.salt,
				"firstName" -> doc.firstName,
				"lastName" -> doc.lastName,
				"avatarUrl" -> doc.avatarUrl,
				"location" -> doc.location,
				"isActive" -> doc.isActive,
				"isAdmin" -> doc.isAdmin,
				"lastUpdatedDateTime" -> Some(DateTime.now).map(date => BSONDateTime(date.getMillis)),
				"lastLoginDateTime" -> doc.lastLoginDateTime.map(date => BSONDateTime(date.getMillis)),
				"createdDateTime" -> doc.createdDateTime.orElse(Some(DateTime.now)).map(date => BSONDateTime(date.getMillis))
			)
		}
	}

}