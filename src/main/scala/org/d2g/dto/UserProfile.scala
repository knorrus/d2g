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

	def avatar: Option[String]
}

case class PublicProfileDTO(
														 id: Option[String],
														 login: String,
														 firstName: String,
														 lastName: String,
														 avatar: Option[String]
														 ) extends UserProfile {

	override def toString: String = "User $id, public profile, how it's visible to rest of world: " + toString()
}

case class PrivateProfileDTO(
															id: Option[String],
															login: String,
															firstName: String,
															lastName: String,
															avatar: Option[String],
															password: Option[String],
															location: String,
															registeredOn: Option[Long]
															) extends UserProfile {

	override def toString: String = "User $id, private profile, how it's visible to itself: " + toString()
}

case class AdminProfileDTO(
														id: Option[String],
														login: String,
														firstName: String,
														lastName: String,
														avatar: Option[String],
														password: Option[String],
														location: String,
														isActive: Boolean = false,
														isAdmin: Boolean = false,
														createdDateTime: Option[Long],
														lastUpdatedDateTime: Option[Long],
														lastLoginDateTime: Option[Long]
														) extends UserProfile {

	override def toString: String = "User $id, private profile, how it's visible to admin: " + toString()
}