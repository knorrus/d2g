package org.d2g.connector

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 16/03/2014
 */
trait ConnectorPlugin {

	def initialize(system: ActorSystem): Unit

	def shutdown(): Unit

	def executor: ExecutionContext

	def instance: Any
}
