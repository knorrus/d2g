package org.d2g.domain

import reactivemongo.bson._

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
case class User(id: Option[BSONObjectID] = None, name: String, password: String)

object User {

	implicit object UserReader extends BSONDocumentReader[User] {
		def read(doc: BSONDocument): User = {
			val id = doc.getAs[BSONObjectID]("_id")
			val name = doc.getAs[String]("name").get
			val password = doc.getAs[String]("password").get
			User(id, name, password)
		}
	}

	implicit object UserWriter extends BSONDocumentWriter[User] {
		def read(doc: BSONDocument): User = {
			val id = doc.getAs[BSONObjectID]("_id")
			val name = doc.getAs[String]("name").get
			val password = doc.getAs[String]("password").get
			User(id, name, password)
		}

		def write(doc: User): BSONDocument = {
			BSONDocument(
				"_id" -> doc.id.getOrElse(BSONObjectID.generate),
				"name" -> BSONString(doc.name),
				"password" -> BSONString(doc.password)
			)
		}
	}

}