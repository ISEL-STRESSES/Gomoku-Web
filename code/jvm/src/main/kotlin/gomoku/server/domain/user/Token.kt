package gomoku.server.domain.user

import kotlinx.datetime.Instant

/**
 * Represents an encoded token
 */
data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant,
    val ttl: Int = DEFAULT_TTL
) {
    companion object {
        const val DEFAULT_TTL = 30 * 60 // 30 min
    }
}
