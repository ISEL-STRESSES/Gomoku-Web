package gomoku.server.repository.user

import gomoku.server.services.user.daos.UserDAO
import gomoku.server.services.user.daos.UserDetailsDAO
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository

@Repository
class JDBIUserRepository(private val handle: Handle) : UserRepository {

    override fun findUserById(uuid: Int): UserDAO? =
        handle.createQuery("SELECT * FROM users WHERE uuid = :uuid")
            .bind("uuid", uuid)
            .mapTo(UserDAO::class.java)
            .singleOrNull()

    override fun findUserByUsername(username: String): UserDAO? =
        handle.createQuery("SELECT * FROM users WHERE username = :username")
            .bind("username", username)
            .mapTo(UserDAO::class.java)
            .singleOrNull()

    override fun findUsers(offset: Int, limit: Int): List<UserDAO> =
        handle.createQuery("SELECT * FROM users LIMIT :limit OFFSET :offset")
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo(UserDAO::class.java)
            .list()

    override fun saveUser(username: String): Int =
        handle.createUpdate("insert into users (username) values (:username)")
            .bind("username", username)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

    override fun getUserDetails(uuid: Int): UserDetailsDAO? =
        handle.createQuery("SELECT *, (select * from stats where uuid = :uuid), (select ) FROM user WHERE uuid = :uuid")
            .bind("uuid", uuid)
            .mapTo(UserDetailsDAO::class.java)
            .singleOrNull()
}
