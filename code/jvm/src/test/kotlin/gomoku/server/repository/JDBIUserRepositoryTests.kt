package gomoku.server.repository

import gomoku.server.TestClock
import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.testWithHandleAndRollback
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.random.Random

// Don't forget to ensure DBMS is up (e.g. by running ./gradlew dbTestsWait)
class JDBIUserRepositoryTests {

    @Test
    fun `can create and retrieve user`() = testWithHandleAndRollback { handle ->
        // given: a UserRepository
        val repo = JDBIUserRepository(handle)

        // when: storing a user
        val userName = newTestUserName()
        val passwordValidationInfo = newTestUserPassword()
        repo.storeUser(userName, passwordValidationInfo)

        // and: retrieving a user
        val retrievedUser: User? = repo.getUserByUsername(userName)

        // then:
        assertNotNull(retrievedUser)
        assertEquals(userName, retrievedUser!!.username)
        assertEquals(passwordValidationInfo, retrievedUser.passwordValidationInfo)
        assertTrue(retrievedUser.uuid >= 0)

        // when: asking if the user exists
        val isUserIsStored = repo.isUserStoredByUsername(userName)

        // then: response is true
        assertTrue(isUserIsStored)

        // when: asking if a different user exists
        val anotherUserIsStored = repo.isUserStoredByUsername("another-$userName")

        // then: response is false
        assertFalse(anotherUserIsStored)
    }

    @Test
    fun `can create and validate tokens`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)
        // and: a test clock
        val clock = TestClock()

        // and: a createdUser
        val userName = newTestUserName()
        val passwordValidationInfo = newTestUserPassword()
        val userId = repo.storeUser(userName, passwordValidationInfo)

        // and: test TokenValidationInfo
        val testTokenValidationInfo = TokenValidationInfo(newTokenValidationData())

        // when: creating a token
        val tokenCreationInstant = clock.now()
        val token = Token(
            testTokenValidationInfo,
            userId,
            createdAt = tokenCreationInstant,
            lastUsedAt = tokenCreationInstant
        )
        repo.createToken(token, 1)

        // then: createToken does not throw errors
        // no exception

        // when: retrieving the token and associated user
        val userAndToken = repo.getTokenAndUserByTokenValidationInfo(testTokenValidationInfo)

        // then:
        val (user, retrievedToken) = userAndToken ?: fail("token and associated user must exist")

        // and: ...
        assertEquals(userName, user.username)
        assertEquals(testTokenValidationInfo.validationInfo, retrievedToken.tokenValidationInfo.validationInfo)
        assertEquals(tokenCreationInstant, retrievedToken.createdAt)
    }

    @Test
    fun `can remove tokens`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)
        // and: a test clock
        val clock = TestClock()

        // and: a createdUser
        val userName = newTestUserName()
        val passwordValidationInfo = newTestUserPassword()
        val userId = repo.storeUser(userName, passwordValidationInfo)

        // and: test TokenValidationInfo
        val testTokenValidationInfo = TokenValidationInfo(newTokenValidationData())

        // when: creating a token
        val tokenCreationInstant = clock.now()
        val token = Token(
            testTokenValidationInfo,
            userId,
            createdAt = tokenCreationInstant,
            lastUsedAt = tokenCreationInstant
        )
        repo.createToken(token, 1)

        // then: createToken does not throw errors
        // no exception

        // when: removing the token
        val removedTokens = repo.removeTokenByTokenValidationInfo(testTokenValidationInfo)

        // then: the token is removed
        assertEquals(1, removedTokens)

        // when: retrieving the token and associated user
        val userAndToken = repo.getTokenAndUserByTokenValidationInfo(testTokenValidationInfo)

        // then: the token and associated user do not exist
        assertEquals(null, userAndToken)
    }

    @Test
    fun `can retrieve users stats`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)
        // and: a rule
        val usersStats = repo.searchRanking(2, "", 0, 10)
        println("----- USERS STATS -----")
        usersStats.forEach(::println)
        // then: users stats are retrieved
        // Check if the returned list is not null
        assertNotNull(usersStats)

        // Check if the size of the returned list is as expected
        assertEquals(10, usersStats.size)

        // Check if pagination is correct
        assertEquals(1, usersStats.first().uuid)
        assertEquals(10, usersStats.last().uuid)

        val userStats = repo.getUserStats(1)
        assertNotNull(userStats)
        assertEquals(1, userStats!!.uuid)
        println("----- USER1 STATS -----")
        userStats.userRuleStats.forEach(::println)

        val userRuleStats = repo.getUserRanking(1, 2)
        assertNotNull(userRuleStats)
        assertEquals(2, userRuleStats!!.ruleId)
        assertEquals(1500, userRuleStats.elo)
        assertEquals(5, userRuleStats.gamesPlayed)
    }

    @Test
    fun `isUserStoredByUsername returns false when user does not exist`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)

        // when: asking if a user exists
        val isUserIsStored = repo.isUserStoredByUsername("non-existing-user")

        // then: response is false
        assertFalse(isUserIsStored)
    }

    @Test
    fun `isUserStoredByUsername returns true when user does exist`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)

        // and: a createdUser
        val userName = newTestUserName()
        val passwordValidationInfo = newTestUserPassword()
        repo.storeUser(userName, passwordValidationInfo)

        // when: asking if the user exists
        val isUserIsStored = repo.isUserStoredByUsername(userName)

        // then: response is true
        assertTrue(isUserIsStored)
    }

    @Test
    fun `isUserStoredById returns false when user does not exist`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)

        // when: asking if a user exists
        val isUserIsStored = repo.isUserStoredById(0)

        // then: response is false
        assertFalse(isUserIsStored)
    }

    @Test
    fun `isUserStoredById returns true when user does exist`() = testWithHandleAndRollback { handle ->
        // given: a UsersRepository
        val repo = JDBIUserRepository(handle)

        // and: a createdUser
        val userName = newTestUserName()
        val passwordValidationInfo = newTestUserPassword()
        val userId = repo.storeUser(userName, passwordValidationInfo)

        // when: asking if the user exists
        val isUserIsStored = repo.isUserStoredById(userId)

        // then: response is true
        assertTrue(isUserIsStored)
    }

    companion object {

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTestUserPassword() = PasswordValidationInfo("password-${abs(Random.nextLong())}")

        private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"
    }
}
