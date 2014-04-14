package org.d2g.connector

import reactivemongo.api.{DB, MongoDriver}
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import reactivemongo.core.actors.Authenticate
import org.d2g.activerecord.UnexpectedServiceException

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
object ReactiveMongo extends ConnectorPlugin {

	val DEFAULT_HOST = "localhost:27017"

	private var _impl: Option[ReactiveMongoImpl] = None

	def instance = _impl.getOrElse(throw new UnexpectedServiceException("ReactiveMongo wasn't initialized"))

	def initialize(system: ActorSystem) = {
		_impl = {
			try {
				Some(new ReactiveMongoImpl(system, List("localhost:27017")))
			} catch {
				case e: Throwable =>
					throw new UnexpectedServiceException("ReactiveMongo initialization error", e)
			}
		}
	}

	def shutdown(): Unit = instance.close()

	def executor: ExecutionContext = instance.ec
}

private[connector] class ReactiveMongoImpl(system: ActorSystem, nodes: Seq[String], authentications: Seq[Authenticate] = Seq.empty, channelsPerNode: Int = 5) {

	implicit val ec: ExecutionContext = system.dispatcher

	private val driver = new MongoDriver
	private val connection = driver.connection(nodes, authentications, channelsPerNode)
	private val database = connection.db("d2g")
	
	def db: DB = database

	def close(): Unit = {
		connection.close()
		driver.close()
	}
}