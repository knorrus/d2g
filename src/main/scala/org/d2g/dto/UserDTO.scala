package org.d2g.dto

/**
	* @author knorr
	* @version 0.1.0-SNAPSHOT
	* @since 05/01/2014
	*/
case class UserDTO(id: Option[String],
										username: String,
										email: String,
										firstName: String,
										lastName: String,
										avatarUrl: Option[String],
										location: String) {
										 
											 override def toString: String = "UserDTO(id: $_id, name: $name)"
										 }
