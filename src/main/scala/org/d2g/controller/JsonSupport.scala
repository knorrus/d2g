package org.d2g.controller

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/01/2014
 */
trait JsonSupport extends NativeJsonSupport {

	protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal

}
