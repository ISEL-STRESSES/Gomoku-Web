package gomoku.server.http.controllers.user

import gomoku.server.http.URIs
import gomoku.server.http.controllers.models.Problem
import gomoku.server.http.controllers.models.user.InputModels.UserLoginInputModel
import gomoku.server.http.controllers.models.user.InputModels.UserRegisterInputModel
import gomoku.server.http.controllers.models.user.OutputModels.GetUserOutputModel
import gomoku.server.http.controllers.models.user.OutputModels.GetUsersOutputModel
import gomoku.server.http.controllers.models.user.OutputModels.UserTokenCreateOutputModel
import gomoku.server.services.errors.TokenCreationError
import gomoku.server.services.errors.UserCreationError
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
    fun ranking(
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10
    ): ResponseEntity<GetUsersOutputModel> {
        val users = service.getUsersData(offset, limit)
        return ResponseEntity.ok(GetUsersOutputModel(users))
    }

    @GetMapping(URIs.Users.GET_BY_ID)
    fun getById(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val user = service.getUserById(id)
        return user?.run {
            ResponseEntity.ok(GetUserOutputModel(this))
        } ?: Problem.response(404, Problem.userNotFound)
    }

    @PostMapping(URIs.Users.CREATE)
    fun create(
        @Valid @RequestBody
        userInput: UserRegisterInputModel
    ): ResponseEntity<*> {
        val res = service.createUser(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    URIs.Users.getByID(res.value).toASCIIString()
                ).build<Unit>()
            is Failure -> when(res.value){
                UserCreationError.InvalidUsername -> Problem.response(400, Problem.invalidUsername)
                UserCreationError.InvalidPassword -> Problem.response(400, Problem.insecurePassword)
                UserCreationError.UsernameAlreadyExists -> Problem.response(409, Problem.userAlreadyExists)
            }
        }
    }

    @PostMapping(URIs.Users.TOKEN)
    fun token(
        @Valid @RequestBody
        userInput: UserLoginInputModel
    ): ResponseEntity<*> {
        val res = service.createToken(username = userInput.username, password = userInput.password)
        return when (res) {
            is Success -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value.tokenValue))
            is Failure -> when(res.value){
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
}
