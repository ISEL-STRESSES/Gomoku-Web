package gomoku.server.domain.user

import kotlinx.datetime.Instant

/**
 * Represents an encoded token
 */
data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)
