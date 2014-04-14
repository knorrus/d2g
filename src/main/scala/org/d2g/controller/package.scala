package org.d2g.controller

import javax.servlet.http.HttpServletRequest

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 14/03/2014
 */
object UrlUtils {


	def createUrlForUser(userId: String)(implicit req: HttpServletRequest) =
		s"${req.getScheme}://${req.getServerName}:${req.getServerPort}${req.getServletPath}/$userId"


}
