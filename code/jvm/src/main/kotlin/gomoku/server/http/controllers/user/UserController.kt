package gomoku.server.http.controllers.user

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.controllers.user.models.UserByIdOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersRankingDataOutputModel
import gomoku.server.http.controllers.user.models.userCreate.UserCreateInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserCreateTokenInputModel
import gomoku.server.http.responses.GetRanking
import gomoku.server.http.responses.GetUserById
import gomoku.server.http.responses.GetUserRanking
import gomoku.server.http.responses.GetUserStats
import gomoku.server.http.responses.Login
import gomoku.server.http.responses.Logout
import gomoku.server.http.responses.SignUp
import gomoku.server.http.responses.UserMe
import gomoku.server.http.responses.response
import gomoku.server.repository.user.UserRankingError
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingServiceError
import gomoku.server.services.user.UserCreateOutputModel
import gomoku.server.services.user.UserService
import gomoku.utils.Failure
import gomoku.utils.Success
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for user-related endpoints
 * @property service The user service
 */
@RestController("Users")
@RequestMapping(URIs.Users.ROOT)
class UserController(private val service: UserService) {

    /**
     * Gets the stats of a user
     * @param userId The id of the user
     * @return The stats of the user or a problem if the user does not exist
     */
    @GetMapping(URIs.Users.USER_STATS)
    fun userStats(@PathVariable userId: Int): ResponseEntity<*> {
        val userStats = service.getUserStats(userId)
        return if (userStats == null) {
            Problem.response(404, Problem.userNotFound)
        } else {
            GetUserStats.siren(UserStatsOutputModel(userStats)).response(200)
        }
    }

    /**
     * Gets the ranking of a user for a given rule
     * @param userId The id of the user
     * @param ruleId The id of the rule
     * @return The ranking of the user or a problem if the user does not exist
     */
    @GetMapping(URIs.Users.USER_RANKING)
    fun userRanking(@PathVariable userId: Int, @PathVariable ruleId: Int): ResponseEntity<*> {
        val userRuleStats = service.getUserRanking(userId, ruleId)
        return when (userRuleStats) {
            is Success -> GetUserRanking.siren(UserRuleStatsOutputModel(userRuleStats.value)).response(200)
            is Failure -> userRuleStats.value.resolveProblem()
        }
    }

    /**
     * Gets the ranking of the users for a given rule
     * @param ruleId The id of the rule
     * @param username The username to search
     * @return The ranking of the users or a [Problem] if the rule does not exist
     */
    @GetMapping(URIs.Users.RANKING)
    fun searchRanking(
        @PathVariable ruleId: Int,
        @RequestParam username: String?,
        @RequestParam offset: Int?,
        @RequestParam limit: Int?
    ): ResponseEntity<*> {
        val users =
            service.searchRanking(ruleId, username, offset, limit) ?: return Problem.response(404, Problem.ruleNotFound)
        return GetRanking.siren(
            GetUsersRankingDataOutputModel(
                users.map(::UserRuleStatsOutputModel),
                ruleId,
                username ?: "",
                limit ?: DEFAULT_LIMIT,
                offset ?: DEFAULT_OFFSET,
                users.size
            )
        ).response(200)
    }

    /**
     * Gets the user by its id
     * @param id The id of the user
     * @return The user or a [Problem] if the user does not exist
     */
    @GetMapping(URIs.Users.GET_BY_ID)
    fun getById(@PathVariable id: Int): ResponseEntity<*> {
        val user = service.getUserById(id) ?: return Problem.response(404, Problem.userNotFound)
        return GetUserById.siren(UserByIdOutputModel(user)).response(200)
    }

    /**
     * Creates a user given its username and password
     * @param userInput The user input
     * @return The created user or if not a [Problem]
     */
    @PostMapping(URIs.Users.CREATE)
    fun create(
        @Valid @RequestBody
        userInput: UserCreateInputModel
    ): ResponseEntity<*> {
        val res = service.createUser(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> SignUp.siren(res.value).response(201)

            is Failure -> res.value.resolveProblem()
        }
    }

    /**
     * Creates a token for a user given its username and password, for example to login
     * @param userInput The user input
     * @return The created token or if not a [Problem]
     */
    @PostMapping(URIs.Users.TOKEN)
    fun token(
        @Valid @RequestBody
        userInput: UserCreateTokenInputModel
    ): ResponseEntity<*> {
        val res = service.createToken(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> Login.siren(
                UserCreateOutputModel(
                    service.getUserByToken(res.value.tokenValue)!!.uuid,
                    res.value.tokenValue
                )
            ).response(200)

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    /**
     * Logs out a user given its token
     * @param authenticatedUser The authenticated user
     */
    @PostMapping(URIs.Users.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val didRevoke = service.revokeToken(authenticatedUser.token)
        return if (!didRevoke) {
            Problem.response(403, Problem.tokenNotRevoked)
        } else {
            Logout.siren().response(200)
        }
    }

    /**
     * Gets the home of a user
     * @param authenticatedUser The authenticated user
     */
    @GetMapping(URIs.Users.HOME)
    fun home(authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        UserMe.siren(
            UserHomeOutputModel(
                authenticatedUser.user.uuid,
                authenticatedUser.user.username,
                authenticatedUser.token
            )
        ).response(200)

    /**
     * Resolves a [UserCreationError] to a [ResponseEntity]
     * @return A translated [Problem] based on the [UserCreationError]
     */
    private fun UserCreationError.resolveProblem() =
        when (this) {
            UserCreationError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
            UserCreationError.InvalidPassword -> Problem.response(400, Problem.insecurePassword)
            UserCreationError.UsernameAlreadyExists -> Problem.response(409, Problem.userAlreadyExists)
        }

    /**
     * Resolves a [UserRankingError] to a [ResponseEntity]
     * @return A translated [Problem] based on the [UserRankingError]
     */
    private fun UserRankingServiceError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UserRankingServiceError.UserNotFound -> Problem.response(404, Problem.userNotFound)
            UserRankingServiceError.RuleNotFound -> Problem.response(404, Problem.ruleNotFound)
            UserRankingServiceError.UserStatsNotFound -> Problem.response(404, Problem.userStatsNotFound)
        }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
