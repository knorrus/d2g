package org.d2g.utils

/**
 * @author knorr
 * @version 0.1.0-SNAPSHOT
 * @since 05/03/2014
 */

import java.security.{SecureRandom, MessageDigest}
import scala.util.Random

object SecurityUtils {

	private val Alphabet = "abcdefghijklmnopqrstuvwxyz0123456789"

	private val SaltLength = 18

	private val digest = MessageDigest.getInstance("SHA-256")

	private val random = new Random(new SecureRandom)

	def sha256(s: String): String = {
		digest.digest(s.getBytes)
			.foldLeft("")((s: String, b: Byte) => s +
			Character.forDigit((b & 0xf0) >> 4, 16) +
			Character.forDigit(b & 0x0f, 16))
	}

	def randomSalt: String = Stream.continually(random.nextInt(Alphabet.size)).map(Alphabet).take(SaltLength).mkString

}

