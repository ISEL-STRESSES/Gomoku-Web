package gomoku.server.http.controllers.user

import gomoku.server.http.URIs
import gomoku.server.http.controllers.models.user.InputModels.UserLoginInputModel
import gomoku.server.http.controllers.models.user.InputModels.UserRegisterInputModel
import gomoku.server.http.controllers.models.user.OutputModels.GetUserOutputModel
import gomoku.server.http.controllers.models.user.OutputModels.GetUsersOutputModel
import gomoku.server.http.controllers.models.user.OutputModels.UserLoginOutputModel
import gomoku.server.http.controllers.models.user.OutputModels.UserRegisterOutputModel
import gomoku.server.services.user.UserService
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
    ): ResponseEntity<GetUserOutputModel> {
        val user = service.getUserByToken(id)
        return ResponseEntity.ok(GetUserOutputModel(user))
    }

    @PostMapping(URIs.Users.REGISTER)
    fun registerUser(
        @Valid @RequestBody
        userInput: UserRegisterInputModel
    ): ResponseEntity<UserRegisterOutputModel> {
        val registerOutputData = service.createUser(username = userInput.username, password = userInput.password)

        return ResponseEntity.ok(UserRegisterOutputModel(token = registerOutputData))
    }

    @PostMapping(URIs.Users.LOGIN)
    fun loginUser(
        @Valid @RequestBody
        userInput: UserLoginInputModel
    ): ResponseEntity<UserLoginOutputModel> {
        val loginOutputData = service.loginUser(username = userInput.username, password = userInput.password)
        return ResponseEntity.ok(loginOutputData)
    }

    @PostMapping(URIs.Users.LOGOUT)
    fun logoutUser(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Unit> {
        service.revokeToken(token)
        return ResponseEntity.ok().build()
    }
}
