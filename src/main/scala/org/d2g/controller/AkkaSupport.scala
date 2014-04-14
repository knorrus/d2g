package org.d2g.controller

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import _root_.akka.util.Timeout

import org.scalatra.FutureSupport
import akka.actor.ActorSystem

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 19/03/2014
 */
trait AkkaSupport extends FutureSupport {

	protected implicit val defaultTimeout = Timeout(5 seconds)

	protected implicit def executor: ExecutionContext = system.dispatcher

	implicit val system: ActorSystem



}
