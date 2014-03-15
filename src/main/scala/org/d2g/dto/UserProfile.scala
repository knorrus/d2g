package org.d2g.dto

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
sealed trait UserProfile {

	def id: Option[String]

	def login: String

	def firstName: String

	def lastName: String

	def email: String

	def avatar: Option[String]
}

/**
 * User public profile, how it's visible to rest of world
 *
 * @param id
 * @param login
 * @param firstName
 * @param lastName
 * @param email
 * @param avatar
 */
case class PublicProfile(
													id: Option[String],
													login: String,
													firstName: String,
													lastName: String,
													email: String,
													avatar: Option[String]
													) extends UserProfile {

	override def toString: String = s"PublicProfile($login-$id)@${Integer.toHexString(hashCode)}"
}

/**
 * User private profile, how it's looks to itself.
 *
 * @param id
 * @param login
 * @param firstName
 * @param lastName
 * @param avatar
 * @param password
 * @param location
 * @param email
 * @param registeredOn
 */
case class PrivateProfile(
													 id: Option[String],
													 login: String,
													 firstName: String,
													 lastName: String,
													 avatar: Option[String],
													 password: Option[String],
													 location: String,
													 email: String,
													 registeredOn: Option[Long]
													 ) extends UserProfile {

	override def toString: String = s"PrivateProfile($login-$id)@${Integer.toHexString(hashCode)}"
}

/**
 * User private profile, how it's visible to admin.
 *
 * @param id
 * @param login
 * @param firstName
 * @param lastName
 * @param email
 * @param avatar
 * @param password
 * @param location
 * @param isActive
 * @param isAdmin
 * @param createdDateTime
 * @param lastUpdatedDateTime
 * @param lastLoginDateTime
 */
case class AdminProfile(
												 id: Option[String],
												 login: String,
												 firstName: String,
												 lastName: String,
												 email: String,
												 avatar: Option[String],
												 password: Option[String],
												 location: String,
												 isActive: Boolean = false,
												 isAdmin: Boolean = false,
												 createdDateTime: Option[Long],
												 lastUpdatedDateTime: Option[Long],
												 lastLoginDateTime: Option[Long]
												 ) extends UserProfile {

	override def toString: String = s"AdminProfile($login-$id)@${Integer.toHexString(hashCode)}"
}

/**
 * User login credentials. Tsssss keep it secret!
 *
 * @param email
 * @param password
 */
case class UserLoginCredentials(email: String, password: String) {

	override def toString: String = s"UserCredentials($email)"
}
