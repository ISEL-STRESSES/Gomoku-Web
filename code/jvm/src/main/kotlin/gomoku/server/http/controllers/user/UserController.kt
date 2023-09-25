package gomoku.server.http.controllers.user

import gomoku.server.http.URIs
import gomoku.server.http.controllers.user.models.get.GetUserOutputModel
import gomoku.server.http.controllers.user.models.get.GetUsersOutputModel
import gomoku.server.http.controllers.user.models.login.UserLoginInputModel
import gomoku.server.http.controllers.user.models.login.UserLoginOutputModel
import gomoku.server.http.controllers.user.models.register.UserRegisterInputModel
import gomoku.server.http.controllers.user.models.register.UserRegisterOutputModel
import gomoku.server.services.user.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(URIs.Users.ROOT)
class UserController(
    private val service: UserService
) {

    @GetMapping(URIs.Users.RANKING)
    fun getRanking(
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10
    ): ResponseEntity<GetUsersOutputModel> {
        val users = service.getRanking(offset, limit)
        return ResponseEntity.ok(GetUsersOutputModel(users))
    }

    @GetMapping(URIs.Users.BY_ID)
    fun getUser(
        @PathVariable id: Int
    ): ResponseEntity<GetUserOutputModel> {
        val user = service.getUser(id)
        return ResponseEntity.ok(GetUserOutputModel(user))
    }

}