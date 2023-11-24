package gomoku.server.domain.user

import gomoku.server.domain.user.User.Companion.MAX_NAME_SIZE
import gomoku.server.domain.user.User.Companion.MAX_PASSWORD_SIZE
import gomoku.server.domain.user.User.Companion.MIN_NAME_SIZE
import gomoku.server.domain.user.User.Companion.MIN_PASSWORD_SIZE
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

/**
 * Represents the class where all the business logic related to users is located
 * @property passwordEncoder The password encoder used to encode and decode passwords
 * @property tokenEncoder The token encoder used to encode and decode tokens
 * @property config The users domain config
 */
@Component
class UsersDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig
) {

    companion object {

        private val usernameRegex = "^[\\S]{$MIN_NAME_SIZE,$MAX_NAME_SIZE}".toRegex()
        private val passwordRegex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&#])[A-Za-z\\d\$@\$!%*?&#]{$MIN_PASSWORD_SIZE,$MAX_PASSWORD_SIZE}\$".toRegex()
    }

    /**
     * Checks if the given name is valid to be used as a username
     * @param name the name to check
     * @return true if the name is valid, false otherwise
     */
    fun isUsernameValid(name: String): Boolean {
        return usernameRegex.matches(name)
    }

    /**
     * Generates a token value
     * @return the generated token value
     */
    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    /**
     * Checks if a token can be a valid token
     * @param token the token to check
     * @return true if the token can be a valid token, false otherwise
     */
    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == config.tokenSizeInBytes
    } catch (ex: IllegalArgumentException) {
        false
    }

    /**
     * Checks if a password is valid with the given validation info
     * @param password the password to check
     * @param validationInfo the password validation info
     * @return true if the password is valid, false otherwise
     */
    fun validatePassword(password: String, validationInfo: PasswordValidationInfo) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo
    )

    /**
     * Creates a password validation info from a password
     * @param password the password to encode
     * @return the password validation info
     */
    fun createPasswordValidationInfo(password: String) = PasswordValidationInfo(
        validationInfo = passwordEncoder.encode(password)
    )

    /**
     * Checks if a token is valid
     * @param clock the clock to use
     * @param token the token to check
     * @return true if the token is valid, false otherwise
     */
    fun isTokenTimeValid(
        clock: Clock,
        token: Token
    ): Boolean {
        val now = clock.now()
        return token.createdAt <= now &&
            (now - token.createdAt) <= config.tokenTtl &&
            (now - token.lastUsedAt) <= config.tokenRollingTtl
    }

    /**
     * Gets the token expiration time
     * @param token the token to check
     * @return the token expiration time
     */
    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }

    /**
     * Creates a token validation info from a token
     * @param token the token to encode
     * @return the token validation info
     */
    fun createTokenValidationInfo(token: String): TokenValidationInfo =
        tokenEncoder.createValidationInformation(token)

    /**
     * Checks if a password is safe
     * @param password the password to check
     * @return true if the password is safe, false otherwise
     */
    fun isSafePassword(password: String) = passwordRegex.matches(password)

    val maxNumberOfTokensPerUser = config.maxTokensPerUser
}
