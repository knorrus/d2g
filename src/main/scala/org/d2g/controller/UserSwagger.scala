package org.d2g.controller

import org.scalatra.swagger.{NativeSwaggerBase, Swagger}

import org.scalatra.ScalatraServlet
import org.json4s.{Formats, DefaultFormats}

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 17/02/2014
 */
class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with NativeSwaggerBase {
	implicit override val jsonFormats: Formats = DefaultFormats
}

class ApiSwagger extends Swagger("1.0", "1") {
}
