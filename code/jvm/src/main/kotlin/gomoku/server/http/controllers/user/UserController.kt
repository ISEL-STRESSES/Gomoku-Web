package gomoku.server.http.controllers.user

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.domain.user.UsersDomainConfig
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersRankingDataOutputModel
import gomoku.server.http.controllers.user.models.userCreate.UserCreateInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserCreateTokenInputModel
import gomoku.server.http.responses.GetRanking
import gomoku.server.http.responses.GetUserRanking
import gomoku.server.http.responses.GetUserStats
import gomoku.server.http.responses.LoginWithCookie
import gomoku.server.http.responses.LoginWithoutCookie
import gomoku.server.http.responses.Logout
import gomoku.server.http.responses.SignUpWithCookie
import gomoku.server.http.responses.SignUpWithoutCookie
import gomoku.server.http.responses.UserMe
import gomoku.server.http.responses.response
import gomoku.server.http.responses.responseRedirect
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingError
import gomoku.server.services.user.UserService
import gomoku.utils.Failure
import gomoku.utils.Success
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.ceil
import kotlin.time.DurationUnit

/**
 * Controller for user-related endpoints
 * @property service The user service
 */
@RestController("Users")
@RequestMapping(URIs.Users.ROOT)
class UserController(private val service: UserService) {

    private val usersDomainConfig: UsersDomainConfig = service.usersDomain.config

    /**
     * Gets the stats of a user
     * @param userId The id of the user
     * @return The stats of the user or a problem if the user does not exist
     */
    @GetMapping(URIs.Users.GET_BY_ID)
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
            is Success -> GetUserRanking.siren(UserRuleStatsOutputModel(userRuleStats.value), ruleId).response(200)
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
        val (users, totalCount) =
            service.searchRanking(ruleId, username, offset, limit) ?: return Problem.response(404, Problem.ruleNotFound)

        val currentOffset = offset ?: DEFAULT_OFFSET
        val currentLimit = limit ?: DEFAULT_LIMIT
        val totalPages = ceil(totalCount.toDouble() / currentLimit).toInt()

        return GetRanking.siren(
            GetUsersRankingDataOutputModel(
                users.map(::UserRuleStatsOutputModel),
                ruleId,
                username ?: ""
            ),
            totalPages,
            currentOffset,
            currentLimit
        ).response(200)
    }

    /**
     * Creates a user given its username and password
     * @param userInput The user input
     * @return The created user or if not a [Problem]
     */
    @PostMapping(URIs.Users.CREATE)
    fun create(
        @RequestBody userInput: UserCreateInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val res = service.createUser(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> {
                if (userInput.sendTokenViaCookie) {
                    setAuthenticationCookies(response, res.value.token, userInput.username)
                    SignUpWithCookie.siren("User created.").responseRedirect(201, URIs.Users.ROOT + URIs.Users.HOME)
                } else {
                    SignUpWithoutCookie.siren(res.value).responseRedirect(201, URIs.Users.ROOT + URIs.Users.HOME)
                }
            }
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
        @RequestBody userInput: UserCreateTokenInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val res = service.createToken(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> {
                if (userInput.sendTokenViaCookie) {
                    setAuthenticationCookies(response, res.value.token, userInput.username)
                    LoginWithCookie.siren("User logged in.").responseRedirect(200, URIs.Users.ROOT + URIs.Users.HOME)
                } else {
                    LoginWithoutCookie.siren(res.value).responseRedirect(200, URIs.Users.ROOT + URIs.Users.HOME)
                }
            }
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
    fun logout(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val didRevoke = service.revokeToken(authenticatedUser.token)
        return if (!didRevoke) {
            Problem.response(403, Problem.tokenNotRevoked)
        } else {
            clearAuthenticationCookies(response)
            Logout.siren().responseRedirect(200, URIs.HOME)
        }
    }

    /**
     * Gets the home of a user
     * @param authenticatedUser The authenticated user
     */
    @GetMapping(URIs.Users.HOME)
    fun home(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val userStats = service.getUserStats(authenticatedUser.user.uuid)
        return UserMe.siren(
            UserHomeOutputModel(
                authenticatedUser.user.uuid,
                authenticatedUser.user.username,
                userStats?.userRuleStats ?: emptyList()
            )
        ).response(200)
    }

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
    private fun UserRankingError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UserRankingError.UserNotFound -> Problem.response(404, Problem.userNotFound)
            UserRankingError.RuleNotFound -> Problem.response(404, Problem.ruleNotFound)
            UserRankingError.UserStatsNotFound -> Problem.response(404, Problem.userStatsNotFound)
        }


    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10

        private fun HttpServletResponse.addCookie(cookie: ResponseCookie) {
            this.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        }
    }

    private fun setAuthenticationCookies(
        response: HttpServletResponse,
        userToken: String,
        username: String
    ) {
        val accessTokenCookie = ResponseCookie.from("tokenCookie", userToken)
            .httpOnly(true)
            .path("/")
            .maxAge(usersDomainConfig.tokenTtl.toLong(DurationUnit.SECONDS))
            .sameSite("Strict")
            .build()

        val usernameCookie = ResponseCookie.from("usernameCookie", username)
            .httpOnly(false)
            .path("/")
            .maxAge(usersDomainConfig.tokenTtl.toLong(DurationUnit.SECONDS))
            .sameSite("Strict")
            .build()

        response.addCookie(accessTokenCookie)
        response.addCookie(usernameCookie)
    }

    private fun clearAuthenticationCookies(response: HttpServletResponse) {
        val accessTokenCookie = ResponseCookie.from("tokenCookie", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()

        val usernameCookie = ResponseCookie.from("usernameCookie", "")
            .httpOnly(false)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()

        response.addCookie(accessTokenCookie)
        response.addCookie(usernameCookie)
    }

}
