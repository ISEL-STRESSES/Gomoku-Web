package gomoku.server.services

import gomoku.server.TestClock
import gomoku.server.domain.user.Sha256TokenEncoder
import gomoku.server.domain.user.UsersDomain
import gomoku.server.domain.user.UsersDomainConfig
import gomoku.server.jbdiTest
import gomoku.server.repository.jdbi.JDBITransactionManager
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.user.UserService
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration

class UserServicesTests {

    private val transactionManager = JDBITransactionManager(jbdiTest())
    private val usersDomain = UsersDomain(
        BCryptPasswordEncoder(),
        Sha256TokenEncoder(),
        UsersDomainConfig(10, Duration.INFINITE, Duration.INFINITE, 10)
    )
    private val clock = TestClock()
    private val userService = UserService(
        transactionManager = transactionManager,
        usersDomain = usersDomain,
        clock = clock
    )

    @Test
    fun `createUser should create a user with valid username and password`() {
        val username = "userTest"
        val password = "ByQYP78&j7Aug2" //Random password that uses a caps, a number and a special character

        testWithTransactionManagerAndRollback { transactionManager ->
            val userService = UserService(
                transactionManager = transactionManager,
                usersDomain = usersDomain,
                clock = clock
            )

            val result = userService.createUser(username, password)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `createUser should fail for invalid username`() {
        val username = "1"
        val password = "ByQYP78&j7Aug2"
        val result = userService.createUser(username, password)

        assertTrue(result is Failure)
        assertEquals(UserCreationError.InvalidUsername, result.value)
    }

    @Test
    fun `createUser should fail for invalid password`() {
        val username = "userTest"
        val password = "1"

        val result = userService.createUser(username, password)

        assertTrue(result is Failure)
        assertEquals(UserCreationError.InvalidPassword, result.value)
    }

    @Test
    fun `createUser should fail for existing username`() {
        val existingUsername = "user1"
        val password = "ByQYP78&j7Aug2"

        val result = userService.createUser(existingUsername, password)

        assertTrue(result is Failure)
        assertEquals(UserCreationError.UsernameAlreadyExists, result.value)
    }

    @Test
    fun `createToken should create a token for valid username and password`() {
        val username = "userTest"
        val password = "ByQYP78&j7Aug2"

        testWithTransactionManagerAndRollback { transactionManager ->
            val userService = UserService(
                transactionManager = transactionManager,
                usersDomain = usersDomain,
                clock = clock
            )

            userService.createUser(username, password)
            val result = userService.createToken(username, password)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `createToken should fail for invalid username or password`() {
        val invalidUsername = "1"
        val invalidPassword = "1"

        val result = userService.createToken(invalidUsername, invalidPassword)

        assertTrue(result is Failure)
        assertEquals(TokenCreationError.UserOrPasswordInvalid, result.value)
    }

    @Test
    fun `getRanking should return user stats for a specific rule with valid offset and limit`() {
        val ruleId = 1
        val offset = 0
        val limit = 10

        val result = userService.getRanking(ruleId, offset, limit)

        assertNotNull(result)
        assertEquals(10, result.size)
        assertEquals(1, result.first().uuid)
        assertEquals(10, result.last().uuid)
    }

    @Test
    fun `getRanking should return user stats with default offset and limit`() {
        val ruleId = 2

        val result = userService.getRanking(ruleId)

        assertNotNull(result)
        assertEquals(10, result.size)
    }

    @Test
    fun `getUserStats should return user stats for valid userId`() {
        val userId = 1

        val result = userService.getUserStats(userId)

        assertNotNull(result)
        assertEquals(1, result.uuid)
        assertEquals("user1", result.username)
    }

    @Test
    fun `getUserStats should return null for invalid userId`() {
        val invalidUserId = -1

        val result = userService.getUserStats(invalidUserId)

        assertNull(result)
    }

    @Test
    fun `getUserRanking should return user ranking for valid userId and ruleId`() {
        val userId = 1
        val ruleId = 1

        val result = userService.getUserRanking(userId, ruleId)

        assertNotNull(result)
        assertEquals(1500, result.elo)
        assertEquals(5, result.gamesPlayed)
    }

    @Test
    fun `getUserRanking should return null for invalid userId or ruleId`() {
        val invalidUserId = -1
        val invalidRuleId = 6

        val result = userService.getUserRanking(invalidUserId, invalidRuleId)

        assertNull(result)
    }

    @Test
    fun `searchRanking should return user stats with valid username, ruleId, offset, and limit`() {
        val ruleId = 1
        val username = "user2"
        val offset = 0
        val limit = 10

        val result = userService.searchRanking(ruleId, username, offset, limit)

        assertNotNull(result)
        // TODO: Add more assertions based on the expected result
    }

    @Test
    fun `searchRanking should return empty list for invalid username, ruleId, offset, or limit`() {
        val invalidRuleId = 6
        val invalidUsername = "12"
        val invalidOffset = -1
        val invalidLimit = -200

        val result = userService.searchRanking(invalidRuleId, invalidUsername, invalidOffset, invalidLimit)

        assertNull(result)
    }

    @Test
    fun `getUserById should return user for valid userId`() {
        val userId = 1

        val result = userService.getUserById(userId)

        assertNotNull(result)
        assertEquals(1, result.uuid)
        assertEquals("user1", result.username)
    }

    @Test
    fun `getUserById should return null for invalid userId`() {
        val invalidUserId = 9999

        val result = userService.getUserById(invalidUserId)

        assertNull(result)
    }

    @Test
    fun `getUserByToken should return user for valid token`() {
        val validToken = "9_ASchOpibhyN4eOl4iIFeBfsDi3Z2fgO9J2837J7Hg="

        val result = userService.getUserByToken(validToken)

        assertNotNull(result)
        assertEquals(2, result.uuid)
        assertEquals("user2", result.username)
    }

    @Test
    fun `getUserByToken should return null for invalid token`() {
        val invalidToken = "abc"

        val result = userService.getUserByToken(invalidToken)

        assertNull(result)
    }

    @Test
    fun `revokeToken should revoke valid token`() {
        val validToken = "9_ASchOpibhyN4eOl4iIFeBfsDi3Z2fgO9J2837J7Hg="

        testWithTransactionManagerAndRollback { transactionManager ->
            val userService = UserService(
                transactionManager = transactionManager,
                usersDomain = usersDomain,
                clock = clock
            )

            userService.revokeToken(validToken)

            val result = userService.getUserByToken(validToken)

            assertNotNull(result)
        }
    }

    @Test
    fun `revokeToken should do nothing for invalid token`() {
        val invalidToken = "abc"

        userService.revokeToken(invalidToken)

        // TODO(): Add assertions
    }
}