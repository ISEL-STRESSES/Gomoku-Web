package gomoku.server.services.user

/**
 * External info of the token
 * @param tokenValue the token value
 * @param tokenExpiration the token expiration
 */
data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: String
)
