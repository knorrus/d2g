package org.d2g.activerecord.manager

import reactivemongo.bson._
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.indexes.IndexType
import reactivemongo.core.commands.LastError
import reactivemongo.core.errors.DatabaseException
import org.d2g.connector.ReactiveMongo
import org.d2g.activerecord._
import org.d2g.activerecord.OperationNotAllowedException
import reactivemongo.api.indexes.Index
import org.d2g.activerecord.DuplicateResourceException
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 09/01/2014
 */
trait MongoEntityManager[T <: IdentifiableRecord] {

	/**
	 * BSON reader for related model
	 */
	implicit val reader: BSONDocumentReader[T]

	/**
	 * BSON writer for related model
	 */
	implicit val writer: BSONDocumentWriter[T]

	/**
	 * Execution context for ReactiveMongo Futures support (used dispatcher of the existed akka system)
	 */
	implicit val ec: ExecutionContext = ReactiveMongo.instance.executor()

	/**
	 * Get instance of connected to app Mongo database.
	 *
	 * @return Application database
	 */
	protected def db = ReactiveMongo.instance.db()

	/**
	 * Get MongoDB collection for this DAO.
	 *
	 * @return Application database collection to work with
	 */
	protected def collection: BSONCollection

	/**
	 * Ensure existence of indexes required for this collection.
	 *
	 * @return Future of booleans list represented index creation statuses
	 */
	protected def ensureIndexes: Future[List[Boolean]]

	/**
	 * Wrap MongoDb LastError object into application specific exceptions
	 *
	 * @param operation operation of Future[LastError]
	 * @param success success handler
	 * @tparam S success return value type
	 * @return Wrapped future of success value or service exception
	 */
	protected def recover[S](operation: Future[LastError])(success: => S): Future[Either[ServiceException, S]] = {
		operation.map {
			lastError => lastError.inError match {
				case true =>
					Left(DBServiceException(lastError))
				case false =>
					Right(success)
			}
		} recover {
			case exception =>
				val handledException: Option[Either[ServiceException, S]] = exception match {
					case e: DatabaseException =>
						e.code.map {
							case 10148 =>
								Left(OperationNotAllowedException("", nestedException = e))
							case 11000 | 11001 =>
								Left(DuplicateResourceException("", nestedException = e))
						}
				}
				handledException.getOrElse(Left(UnexpectedServiceException(exception.getMessage, nestedException = exception)))
		}
	}

	/**
	 * Create collection index if non-exist.
	 *
	 * @param key List that contains pairs with the name of the field or fields to index and order of the index. A 1 specifies ascending and a -1 specifies descending
	 * @param name Name of the index. If unspecified, MongoDB generates an index name by concatenating the names of the indexed fields and the sort order
	 * @param unique Creates a unique index so that the collection will not accept insertion of documents where the index key or keys match an existing value in the index
	 * @param background Builds the index in the background so that building an index does not block other database activities
	 * @param dropDups Creates a unique index on a field that may have duplicates
	 * @param sparse If true, the index only references documents with the specified field
	 * @param version Index version number
	 * @param options Optional parameters for this index (typically specific to an IndexType like Geo2D).
	 * @return Future of boolean representation of operation status
	 */
	def ensureIndex(key: List[(String, IndexType)],
									name: Option[String] = None,
									unique: Boolean = false,
									background: Boolean = false,
									dropDups: Boolean = false,
									sparse: Boolean = false,
									version: Option[Int] = None,
									options: BSONDocument = BSONDocument()) = {
		collection.indexesManager.ensure(Index(key, name, unique, background, dropDups, sparse, version, options))
	}

	/**
	 * Get document by id
	 *
	 * @param id BSON object id
	 * @return Future of model option
	 */
	def findById(id: BSONObjectID): Future[Option[T]] = {
		collection.find(BSONDocument("_id" -> id)).one[T]
	}

	/**
	 * Get document by id
	 *
	 * @param id String object id
	 * @return Future of model option
	 */
	def findById(id: String): Future[Option[T]] = {
		findById(BSONObjectID(id))
	}

	/**
	 * Find document by specified query.
	 *
	 * @param query Specifies selection criteria using query operators. To return all matched documents from collection
	 * @return  Future of model option
	 */
	def findOne(query: BSONDocument): Future[Option[T]] = {
		collection.find(query).one[T]
	}

	/**
	 * Load all documents from collection
	 *
	 * @return Future of models list
	 */
	def findAll(): Future[List[T]] = {
		collection.find(BSONDocument()).cursor[T].collect[List]()
	}

	/**
	 * Find documents by specified field.
	 *
	 * @param attribute Field name
	 * @param value Field value
	 * @return Future of models list
	 */
	def findByAttribute(attribute: String, value: String): Future[List[T]] = {
		collection.find(BSONDocument(attribute -> BSONString(value))).cursor[T].collect[List]()
	}

	/**
	 * Persist document in collection.
	 *
	 * @param document Model to persist in collection
	 * @return Future with newly inserted document in case success, future with service exception otherwise
	 */
	def insert(document: T): Future[Either[ServiceException, T]] = {
		recover(collection.insert(document)) {
			document
		}
	}

	/**
	 * Remove document by id
	 *
	 * @param id BSON document id
	 * @return Future which will be completed with true if document was removed successfully, or with exception otherwise
	 */
	def remove(id: BSONObjectID): Future[Either[ServiceException, Boolean]] = {
		recover(collection.remove(BSONDocument("_id" -> id))) {
			true
		}
	}

	/**
	 * Remove document by id
	 *
	 * @param id String document id
	 * @return Future which will be completed with true if document was removed successfully, or with exception otherwise
	 */
	def remove(id: String): Future[Either[ServiceException, Boolean]] = {
		remove(BSONObjectID(id))
	}

	/**
	 * Remove documents by specified field
	 *
	 * @param attribute Field name
	 * @param value Field value
	 * @return future which will be completed with true if documents was removed successfully, or with exception otherwise
	 */
	def removeWith(attribute: String, value: String) = {
		recover(collection.remove(BSONDocument(attribute -> BSONString(value)))) {
			true
		}
	}

	/**
	 * Drop current collection from database.
	 *
	 * @return Future of boolean representation of operation status
	 */
	def drop = collection.drop
}