package gomoku.server.domain.user

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class UserDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UserDomainConfig
) {

    companion object {
        private const val MIN_NAME_SIZE = 3
        private const val MAX_NAME_SIZE = 20
        private const val MIN_PASSWORD_SIZE = 4
        private const val MAX_PASSWORD_SIZE = 24

        private val usernameRegex = "^[a-zA-Z0-9 ]{$MIN_NAME_SIZE,$MAX_NAME_SIZE}".toRegex()
        private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&#])[A-Za-z\\d\$@\$!%*?&#]{$MIN_PASSWORD_SIZE,$MAX_PASSWORD_SIZE}\$".toRegex()
    }

    /**
     * Checks if the given name is valid
     */
    fun isUsernameValid(name: String): Boolean {
        return usernameRegex.matches(name)
    }
    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == config.tokenSizeInBytes
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun validatePassword(password: String, validationInfo: PasswordValidationInfo) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo
    )

    fun createPasswordValidationInfo(password: String) = PasswordValidationInfo(
        validationInfo = passwordEncoder.encode(password)
    )

    fun isTokenTimeValid(
        clock: Clock,
        token: Token
    ): Boolean {
        val now = clock.now()
        return token.createdAt <= now &&
            (now - token.createdAt) <= config.tokenTtl &&
            (now - token.lastUsedAt) <= config.tokenRollingTtl
    }

    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }

    fun createTokenValidationInfo(token: String): TokenValidationInfo =
        tokenEncoder.createValidationInformation(token)

    fun isSafePassword(password: String) = passwordRegex.matches(password)

    val maxNumberOfTokensPerUser = config.maxTokensPerUser
}
