package gomoku.server.domain.user

/**
 * Interface for token encoders
 * @property createValidationInformation creates validation information from a token
 */
interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
