
package org.d2g.persistence

import reactivemongo.api.{DefaultDB, MongoDriver}
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import reactivemongo.core.actors.Authenticate

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
object ReactiveMongo {
	def apply(system: ActorSystem): ReactiveMongo = new ReactiveMongoImpl(system, List("localhost:27017"))
}

abstract class ReactiveMongo {

	def shutdown(): Unit

	def getDatabase(name: String): DefaultDB
}

private[persistence] class ReactiveMongoImpl(system: ActorSystem, nodes: Seq[String], authentications: Seq[Authenticate] = Seq.empty, channelsPerNode: Int = 10) extends ReactiveMongo {

	private implicit val ec: ExecutionContext = system.dispatcher

	lazy val driver = new MongoDriver
	lazy val connection = driver.connection(nodes, authentications, channelsPerNode)

	def shutdown(): Unit = {
		connection.close()
		driver.close()
	}

	def getDatabase(name: String): DefaultDB = {
		connection.db(name)
	}
}