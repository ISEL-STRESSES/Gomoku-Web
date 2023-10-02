package gomoku.server.repository.authentication

import gomoku.server.domain.user.Password
import gomoku.server.domain.user.Token
import org.jdbi.v3.core.Handle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class JDBIAuthenticationRepository(@Autowired private val handle: Handle) : AuthenticationRepository {
    override fun getToken(uuid: Int): Token? =
        handle.createQuery("SELECT encoded_token FROM token WHERE user_id = :uuid")
            .bind("uuid", uuid)
            .mapTo(Token::class.java)
            .singleOrNull()

    override fun getPassword(uuid: Int): Password? =
        handle.createQuery("SELECT encoded_password FROM password WHERE user_id = :uuid")
            .bind("uuid", uuid)
            .mapTo(Password::class.java)
            .singleOrNull()

    override fun getUserID(token: Token): Int? =
        handle.createQuery("SELECT user_id FROM token WHERE encoded_token = :token")
            .bind("token", token)
            .mapTo(Int::class.java)
            .singleOrNull()

    override fun saveKeys(uuid: Int, token: Token, password: Password) {
        handle.createUpdate("INSERT INTO token(user_id, encoded_token, create_date, last_used, ttl) VALUES (:uuid, :token, now(), now(), :Token.DEFAULT_TTL)")
            .bind("uuid", uuid)
            .bind("token", token)
            .execute()

        handle.createUpdate("INSERT INTO password(user_id, encoded_password, method) VALUES (:uuid, :password, 'sha256')")
            .bind("uuid", uuid)
            .bind("password", password.encodedPassword)
            .execute()
    }

    override fun setToken(uuid: Int, token: Token) {
        handle.createUpdate("INSERT INTO token(user_id, encoded_token, create_date, last_used, ttl) VALUES (:uuid, :token, now(), now(), :Token.DEFAULT_TTL)")
            .bind("uuid", uuid)
            .bind("token", token)
            .execute()
    }

    override fun setPassword(uuid: Int, password: Password) {
        handle.createUpdate("INSERT INTO password(user_id, encoded_password, method) VALUES (:uuid, :password, 'sha256')")
            .bind("uuid", uuid)
            .bind("password", password.encodedPassword)
            .execute()
    }
}
