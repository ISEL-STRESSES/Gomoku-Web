package gomoku.server.http.controllers.user

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.controllers.user.models.UserByIdOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersDataOutputModel
import gomoku.server.http.controllers.user.models.userCreate.UserCreateInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserCreateTokenInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserTokenCreateOutputModel
import gomoku.server.repository.user.UserRankingError
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingServiceError
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
            Problem.response(Problem.userNotFound)
        } else {
            ResponseEntity.ok(UserStatsOutputModel(userStats))
        }
    }

    /**
     * Gets the ranking of a user for a given rule
     * @param userId The id of the user
     * @param ruleId The id of the rule
     * @return The ranking of the user or a problem if the user does not exist
     */
    @GetMapping(URIs.Users.USER_RANKING)
    fun userRanking(@PathVariable userId: Int, @PathVariable ruleId: Int): ResponseEntity<*> { // TODO: TEST THIS FUNCTION
        val userRuleStats = service.getUserRanking(userId, ruleId)
        return when (userRuleStats) {
            is Success -> ResponseEntity.ok(UserRuleStatsOutputModel(userRuleStats.value))
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
    fun searchRanking(@PathVariable ruleId: Int, @RequestParam username: String?, @RequestParam offset: Int?, @RequestParam limit: Int?): ResponseEntity<*> {
        val users = service.searchRanking(ruleId, username) ?: return Problem.response(Problem.ruleNotFound)
        return ResponseEntity.ok(GetUsersDataOutputModel(users.map(::UserRuleStatsOutputModel)))
    }

    /**
     * Gets the user by its id
     * @param id The id of the user
     * @return The user or a [Problem] if the user does not exist
     */
    @GetMapping(URIs.Users.GET_BY_ID)
    fun getById(@PathVariable id: Int): ResponseEntity<*> {
        val user = service.getUserById(id) ?: return Problem.response(Problem.userNotFound)
        return ResponseEntity.ok(UserByIdOutputModel(user))
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
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    URIs.Users.byID(res.value).toASCIIString()
                ).build<Unit>()

            is Failure -> when (res.value) {
                UserCreationError.InvalidUsername -> Problem.response(Problem.invalidUsername)
                UserCreationError.InvalidPassword -> Problem.response(Problem.insecurePassword)
                UserCreationError.UsernameAlreadyExists -> Problem.response(Problem.userAlreadyExists)
            }
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
            is Success -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value.tokenValue))

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordInvalid -> Problem.response(Problem.userOrPasswordAreInvalid)
            }
        }
    }

    /**
     * Logs out a user given its token
     * @param authenticatedUser The authenticated user
     */
    @PostMapping(URIs.Users.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser) {
        service.revokeToken(authenticatedUser.token)
    }

    /**
     * Gets the home of a user
     * @param authenticatedUser The authenticated user
     */
    @GetMapping(URIs.Users.HOME)
    fun home(authenticatedUser: AuthenticatedUser): UserHomeOutputModel {
        return UserHomeOutputModel(
            id = authenticatedUser.user.uuid,
            username = authenticatedUser.user.username
        )
    }

    /**
     * Resolves a [UserRankingError] to a [ResponseEntity]
     * @return A translated [Problem] based on the [UserRankingError]
     */
    private fun UserRankingServiceError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UserRankingServiceError.UserNotFound -> Problem.response(Problem.userNotFound)
            UserRankingServiceError.RuleNotFound -> Problem.response(Problem.ruleNotFound)
            UserRankingServiceError.UserStatsNotFound -> Problem.response(Problem.userStatsNotFound)
        }
}
