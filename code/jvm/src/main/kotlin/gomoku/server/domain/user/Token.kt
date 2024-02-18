package gomoku.server.domain.user

import kotlinx.datetime.Instant

/**
 * Represents an encoded token
 * @property tokenValidationInfo the validation info
 * @property userId the id of the user the token belongs to
 * @property createdAt the time the token was created
 * @property lastUsedAt the time the token was last used
 */
data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)
