package gomoku.server.http.controllers

import gomoku.server.http.URIs
import gomoku.server.domain.Author
import gomoku.server.domain.ServerInfo
import gomoku.server.http.controllers.user.models.login.UserLoginInputModel
import gomoku.server.http.controllers.user.models.login.UserLoginOutputModel
import gomoku.server.http.controllers.user.models.register.UserRegisterInputModel
import gomoku.server.http.controllers.user.models.register.UserRegisterOutputModel
import gomoku.server.services.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class HomeController(
    private val serverInfo: ServerInfo,
    private val service: UserService
) {

    @GetMapping(URIs.HOME)
    fun getHome(request: HttpServletRequest): ResponseEntity<ServerInfo> {
        return ResponseEntity.ok(serverInfo)
    }

    @PostMapping(URIs.REGISTER)
    fun registerUser(
        @Valid @RequestBody userInput: UserRegisterInputModel
    ): ResponseEntity<UserRegisterOutputModel> {
        val registerOutputDTO = service.registerUser(userInput.toUserRegisterInputDTO())

        return ResponseEntity.ok(UserRegisterOutputModel(registerOutputDTO))
    }

    @PostMapping(URIs.LOGIN)
    fun loginUser(
        @Valid @RequestBody userInput: UserLoginInputModel
    ): ResponseEntity<UserLoginOutputModel> {
        val loginOutputDTO = service.loginUser(userInput.toUserLoginInputDTO())

        return ResponseEntity.ok(UserLoginOutputModel(loginOutputDTO))
    }

    @PostMapping(URIs.LOGOUT)
    fun logoutUser(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Unit> {
        service.logoutUser(token)

        return ResponseEntity.ok().build()
    }
}