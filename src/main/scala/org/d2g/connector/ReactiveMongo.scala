
package org.d2g.connector

import reactivemongo.api.{DB, MongoDriver}
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import reactivemongo.core.actors.Authenticate
import org.d2g.activerecord.UnexpectedServiceException
import com.sun.org.apache.bcel.internal.ExceptionConstants

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
object ReactiveMongo {

	val DEFAULT_HOST = "localhost:27017"

	private var _impl: Option[ReactiveMongo] = None

	def instance = _impl.getOrElse(throw new UnexpectedServiceException("ReactiveMongo wasn't initialized"))

	def init(system: ActorSystem) = {
		_impl = {
			try {
				Some(new ReactiveMongoImpl(system, List("localhost:27017")))
			} catch {
				case e: Throwable =>
					throw new UnexpectedServiceException("ReactiveMongo initialization error", e)
			}
		}
		_impl.map {
			connection =>
		}
	}

}

trait ReactiveMongo {

	def shutdown(): Unit

	def setup(): Unit

	def db(): DB

  def executor() : ExecutionContext
}

private[connector] class ReactiveMongoImpl(system: ActorSystem, nodes: Seq[String], authentications: Seq[Authenticate] = Seq.empty, channelsPerNode: Int = 5) extends ReactiveMongo {

	private implicit val ec: ExecutionContext = system.dispatcher

	lazy val driver = new MongoDriver
	lazy val connection = driver.connection(nodes, authentications, channelsPerNode)
	lazy val db = connection.db("d2g")

	def shutdown(): Unit = {
		connection.close()
		driver.close()
	}

	def setup(): Unit = {}

	def executor(): ExecutionContext = ec
}