package org.d2g.activerecord

import reactivemongo.core.commands.LastError
import scala.Predef._
import scala.Some
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Future
import org.d2g.activerecord.manager.MongoEntityManager

trait IdentifiableRecord {

	def _id: Option[BSONObjectID]

	def identify = _id.map(value => value.stringify).getOrElse("")
}

trait ActiveRecord[T <: IdentifiableRecord] {

	def entityManager: MongoEntityManager[T]

	def delete: Future[Either[ServiceException, Boolean]]

	def save: Future[Either[ServiceException, T]]
}

/**
 * Trait for service exceptions.
 */
trait ServiceException extends Exception {
	val message: String
	val nestedException: Throwable
}

/**
 * Generic DAO layer exception.
 *
 * @param message - error message
 * @param nestedException - original exception
 */
case class UnexpectedServiceException(message: String, nestedException: Throwable = null) extends ServiceException

/**
 * Wrapper for MongoDB LastError.
 *
 * @param message - error message
 * @param lastError - mongo error
 * @param nestedException - original exception
 */
case class DBServiceException(message: String, lastError: Option[LastError] = None, nestedException: Throwable = null) extends ServiceException

/**
 * Factory object for wrapping MongoDB errors.
 */
object DBServiceException {
	def apply(lastError: LastError): ServiceException = {
		DBServiceException(lastError.errMsg.getOrElse(lastError.message), Some(lastError))
	}
}

/**
 * MongoDB error codes 11000, 11001: _id values must be unique in a collection.
 *
 * @param message - error message
 * @param nestedException - original exception
 */
case class DuplicateResourceException(
																			 message: String = "error.duplicate.resource",
																			 nestedException: Throwable = null
																			 ) extends ServiceException

/**
 * Operation not permitted exception.
 *
 * @param message - error message
 * @param nestedException - original exception
 */
case class OperationNotAllowedException(
																				 message: String = "error.operation.not.allowed",
																				 nestedException: Throwable = null
																				 ) extends ServiceException

/**
 * Document with specified id not found in collection.
 *
 * @param id - identifier on document
 * @param message - error message
 * @param nestedException - original exception
 */
case class ResourceNotFoundException(
																			id: String,
																			message: String = "error.resource.not.found",
																			nestedException: Throwable = null
																			) extends ServiceException