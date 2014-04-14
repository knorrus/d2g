package org.d2g.connector

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import org.d2g.activerecord.UnexpectedServiceException
import com.redis.{RedisClient, RedisClientPool}

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 16/03/2014
 */
object Redis extends ConnectorPlugin {

	val DEFAULT_HOST = "localhost:6379"

	private var _impl: Option[RedisImpl] = None

	def instance = _impl.getOrElse(throw new UnexpectedServiceException("Redis connection wasn't initialized"))

	def initialize(system: ActorSystem) = {
		_impl = {
			try {
				Some(new RedisImpl(system))
			} catch {
				case e: Throwable =>
					throw new UnexpectedServiceException("Redis initialization error", e)
			}
		}
	}

	def shutdown(): Unit = instance.close()

	def executor: ExecutionContext = instance.ec
}

private[connector] class RedisImpl(system: ActorSystem) {

	private val pool = new RedisClientPool("localhost", 6379)

	implicit val ec: ExecutionContext = system.dispatcher

	def exec(command: RedisClient => Unit):Unit = pool.withClient(command)

	def close() = pool.close
}
