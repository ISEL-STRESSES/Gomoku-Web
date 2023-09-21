package gomoku.server.http.controllers.user

import URIs
import gomoku.server.http.controllers.user.models.register.UserRegisterInputModel
import gomoku.server.http.controllers.user.models.register.UserRegisterOutputModel
import gomoku.server.services.user.UserService
import gomoku.server.services.user.dtos.register.UserRegisterOutputDTO
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(private val service: UserService) {

//    @GetMapping(URIs.Users.ROOT)
//    fun getUsers(
//        @RequestParam offset: Int? = 0,
//        @RequestParam limit: Int? = 10
//    ) : ResponseEntity<List<User>> {
//        val users = service.getUsers()
//        return ResponseEntity.ok(users)
//    }

//    @GetMapping(URIs.Users.BY_ID)
//    fun getUser(@PathVariable id: Int) : ResponseEntity<User> {
//        val user = service.getUser(id)
//        return ResponseEntity.ok(user)
//    }

    @GetMapping(URIs.Users.REGISTER)
    fun registerUser(
        @Valid @RequestBody userInput: UserRegisterInputModel
    ) : ResponseEntity<UserRegisterOutputModel> {
        val registerOutputDTO: UserRegisterOutputDTO = service.registerUser(userInput.toUserRegisterInputDTO())

        return ResponseEntity.ok(UserRegisterOutputModel(registerOutputDTO))
    }

//    @GetMapping(URIs.Users.LOGIN)
//    fun loginUser(
//        @RequestParam username: String,
//        @RequestParam password: String
//    ) : ResponseEntity<User> {
//        val user = service.loginUser(username, password)
//        return ResponseEntity.ok(user)
//    }

}