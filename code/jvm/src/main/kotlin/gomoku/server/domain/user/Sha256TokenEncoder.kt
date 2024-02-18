package gomoku.server.domain.user

import java.security.MessageDigest
import java.util.*

/**
 * Class that encodes tokens using SHA256
 */
class Sha256TokenEncoder : TokenEncoder {

    /**
     * creates a validation info from a token
     * @param token the token to encode
     * @return the encoded token
     */
    override fun createValidationInformation(token: String): TokenValidationInfo =
        TokenValidationInfo(hash(token))

    /**
     * hashes a string using SHA256
     * @param input the string to hash
     * @return the hashed string
     */
    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}
