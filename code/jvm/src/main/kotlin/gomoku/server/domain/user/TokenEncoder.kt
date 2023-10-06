package gomoku.server.domain.user

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
