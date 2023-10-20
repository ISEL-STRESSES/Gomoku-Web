package gomoku.server.domain.user

/**
 * Interface for token encoders
 * @property createValidationInformation creates validation information from a token
 */
interface TokenEncoder {
    /**
     * Creates validation information from a token
     * @param token the token
     * @return the validation information
     */
    fun createValidationInformation(token: String): TokenValidationInfo
}
