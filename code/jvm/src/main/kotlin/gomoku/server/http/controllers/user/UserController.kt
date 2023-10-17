package gomoku.server.http.controllers.user

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.controllers.user.models.UserDataOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersDataOutputModel
import gomoku.server.http.controllers.user.models.userCreate.UserCreateInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserCreateTokenInputModel
import gomoku.server.http.controllers.user.models.userTokenCreate.UserTokenCreateOutputModel
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.user.UserService
import gomoku.utils.Failure
import gomoku.utils.Success
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController(URIs.Users.ROOT)
class UserController(private val service: UserService) {

    @GetMapping(URIs.Users.RANKING)
    fun getUsersRuleData(
        @PathVariable ruleId: Int,
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10
    ): ResponseEntity<*> {
        val users = service.getUsersRuleStats(ruleId, offset, limit)
        return ResponseEntity.ok(GetUsersDataOutputModel(users.map(::UserDataOutputModel)))
    }

    @GetMapping(URIs.Users.USER_STATS)
    fun getUserStats(@PathVariable userId: Int): ResponseEntity<*> {
        val userStats = service.getUserStats(userId)
        return if (userStats == null) {
            Problem.response(404, Problem.userNotFound)
        } else {
            ResponseEntity.ok(UserStatsOutputModel(userStats.uuid, userStats.username, userStats.userRuleStats))
        }
    }

    @GetMapping(URIs.Users.USER_RANKING)
    fun getUserRuleStats(
        @PathVariable userId: Int,
        @PathVariable ruleId: Int
    ): ResponseEntity<*> {
        val userRuleStats = service.getUserRuleStats(userId, ruleId)
        return if (userRuleStats == null) {
            Problem.response(404, Problem.userNotFound)
        } else {
            ResponseEntity.ok(UserRuleStatsOutputModel(userRuleStats.ruleId, userRuleStats.gamesPlayed, userRuleStats.elo))
        }
    }

    @GetMapping(URIs.Users.GET_BY_ID)
    fun getById(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val user = service.getUserById(id)
        return if (user == null) {
            Problem.response(404, Problem.userNotFound)
        } else {
            ResponseEntity.ok(UserDataOutputModel(user))
        }
    }

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
                UserCreationError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                UserCreationError.InvalidPassword -> Problem.response(400, Problem.insecurePassword)
                UserCreationError.UsernameAlreadyExists -> Problem.response(409, Problem.userAlreadyExists)
            }
        }
    }

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
                TokenCreationError.UserOrPasswordInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(URIs.Users.LOGOUT)
    fun logout(
        @RequestHeader("Authorization") token: String
    ) {
        service.revokeToken(token)
    }

    @GetMapping(URIs.Users.HOME)
    fun home(authenticatedUser: AuthenticatedUser): UserHomeOutputModel {
        return UserHomeOutputModel(
            authenticatedUser.user.uuid,
            authenticatedUser.user.username
        )
    }
}
