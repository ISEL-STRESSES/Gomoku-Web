package gomoku.server.services.user

import gomoku.server.services.user.dtos.register.UserRegisterInputDTO
import gomoku.server.services.user.dtos.register.UserRegisterOutputDTO
import org.springframework.stereotype.Service

@Service
class UserService {

    fun registerUser(registerInputDTO: UserRegisterInputDTO): UserRegisterOutputDTO {
        TODO()
        //Will we use JDBC or JPA?
        //Honestly think we should try to use JPA
    }
}