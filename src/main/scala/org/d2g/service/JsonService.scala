package org.d2g.service

import spray.routing.HttpService
import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}

/**
 * @author knorr
 */
trait JsonService extends HttpService with Json4sSupport {

  //Json services always reutrn content-type: application/json
  implicit def json4sFormats: Formats = DefaultFormats

}
