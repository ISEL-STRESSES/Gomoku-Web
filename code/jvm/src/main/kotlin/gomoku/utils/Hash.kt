package gomoku.utils

import java.security.MessageDigest

/**
 * Hashes a string using SHA-256
 *
 * @param input The string to hash
 * @return The hashed string
 */
fun sha256(input: String): String {
    val digester = MessageDigest.getInstance("SHA-256")
    val hashedString = digester.digest(input.toByteArray(Charsets.UTF_8))
    return hashedString.joinToString("") { "%02x".format(it) }
}
