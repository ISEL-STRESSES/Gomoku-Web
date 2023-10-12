package gomoku.server.domain.user

import gomoku.server.TestClock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@ExperimentalTime
class UserDomainTests {

    private lateinit var passwordEncoder: BCryptPasswordEncoder
    private lateinit var tokenEncoder: Sha256TokenEncoder
    private lateinit var usersDomain: UsersDomain

    @BeforeEach
    fun setUp() {
        passwordEncoder = BCryptPasswordEncoder()
        tokenEncoder = Sha256TokenEncoder()
        usersDomain = createUsersDomain(passwordEncoder, tokenEncoder)
    }

    @Test
    fun `isUsernameValid should validate username correctly`() {
        assertTrue(usersDomain.isUsernameValid("validUser"))
        assertFalse(usersDomain.isUsernameValid("in validUser"))
        assertFalse(usersDomain.isUsernameValid("sh"))
    }

    @Test
    fun `generateTokenValue should return valid token`() {
        val token = usersDomain.generateTokenValue()
        assertNotNull(token)
        assertTrue(usersDomain.canBeToken(token))
    }

    @Test
    fun `canBeToken should return false for invalid token`() {
        assertFalse(usersDomain.canBeToken("invalid_token"))
    }

    @Test
    fun `validatePassword should validate password correctly`() {
        val rawPassword = "rawPassword"
        val encodedPassword = passwordEncoder.encode(rawPassword)

        assertTrue(usersDomain.validatePassword(rawPassword, PasswordValidationInfo(encodedPassword)))
    }

    @Test
    fun `createPasswordValidationInfo should return correct info`() {
        val password = "password"
        val validationInfo = usersDomain.createPasswordValidationInfo(password)
        assertTrue(passwordEncoder.matches(password, validationInfo.validationInfo))
    }

    @Test
    fun `isSafePassword should validate strong passwords correctly`() {
        assertTrue(usersDomain.isSafePassword("StrongP@ss1"))
        assertFalse(usersDomain.isSafePassword("weakpass"))
    }

    @Test
    fun `isTokenTimeValid should validate token time correctly`() {
        val currentTime = clock.now()

        // Scenario: Valid token, used recently and created recently
        val validToken = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo1"),
            userId = 1,
            createdAt = currentTime - 2.hours,
            lastUsedAt = currentTime - 30.minutes
        )
        assertTrue(usersDomain.isTokenTimeValid(clock, validToken))

        // Scenario: Token created recently, but not used recently (expired rolling ttl)
        val notUsedRecentlyToken = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo2"),
            userId = 1,
            createdAt = currentTime - 2.hours,
            lastUsedAt = currentTime - (2 * TOKEN_ROLLING_TTL_IN_MINUTES).minutes
        )
        assertFalse(usersDomain.isTokenTimeValid(clock, notUsedRecentlyToken))

        // Scenario: Token used recently, but created a long time ago (expired absolute ttl)
        val oldToken = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo3"),
            userId = 1,
            createdAt = currentTime - (2 * TOKEN_TTL_IN_HOURS).days,
            lastUsedAt = currentTime - 30.minutes
        )
        assertFalse(usersDomain.isTokenTimeValid(clock, oldToken))
    }

    @Test
    fun `getTokenExpiration should return correct expiration time`() {
        val currentTime = clock.now()

        // Scenario: Token's rolling expiration is sooner than absolute expiration
        val token1 = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo4"),
            userId = 1,
            createdAt = currentTime - 10.hours,
            lastUsedAt = currentTime - 30.minutes
        )
        assertEquals(currentTime, usersDomain.getTokenExpiration(token1))

        // Scenario: Token's absolute expiration is sooner than rolling expiration
        val token2 = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo5"),
            userId = 1,
            createdAt = currentTime - 23.hours - 40.minutes,
            lastUsedAt = currentTime
        )
        assertEquals(currentTime + 20.minutes, usersDomain.getTokenExpiration(token2))

        // Scenario: Token's absolute expiration and rolling expiration are the same
        val token3 = Token(
            tokenValidationInfo = TokenValidationInfo("dummyInfo6"),
            userId = 1,
            createdAt = currentTime - 23.hours - 30.minutes,
            lastUsedAt = currentTime - 30.minutes
        )
        assertEquals(currentTime, usersDomain.getTokenExpiration(token3))
    }

    @Test
    fun `createTokenValidationInfo should return correct validation info`() {
        val token = "validToken"
        val validationInfo = usersDomain.createTokenValidationInfo(token)
        assertNotNull(validationInfo)
        assertEquals(validationInfo.validationInfo, tokenEncoder.createValidationInformation(token).validationInfo)
    }

    companion object {

        private const val TOKEN_TTL_IN_HOURS = 24
        private const val TOKEN_ROLLING_TTL_IN_MINUTES = 30
        private const val MAX_TOKENS_PER_USER = 3
        private fun createUsersDomain(
            passwordEncoder: BCryptPasswordEncoder,
            tokenEncoder: Sha256TokenEncoder,
            tokenTtl: Duration = TOKEN_TTL_IN_HOURS.hours,
            tokenRollingTtl: Duration = TOKEN_ROLLING_TTL_IN_MINUTES.minutes,
            maxTokensPerUser: Int = MAX_TOKENS_PER_USER
        ) = UsersDomain(
            passwordEncoder,
            tokenEncoder,
            UsersDomainConfig(
                tokenSizeInBytes = 256 / 8,
                tokenTtl = tokenTtl,
                tokenRollingTtl,
                maxTokensPerUser = maxTokensPerUser
            )
        )

        private val clock = TestClock()
    }
}
