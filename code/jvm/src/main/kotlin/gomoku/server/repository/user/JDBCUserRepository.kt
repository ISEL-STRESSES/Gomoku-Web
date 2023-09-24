package gomoku.server.repository.user

import gomoku.server.services.user.dtos.get.UserDetailOutputDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
class JdbcUserRepository(@Autowired private val jdbcTemplate: JdbcTemplate) : UserRepository {

    override fun findUserById(uuid: Int): UserDetailOutputDTO? {
        return jdbcTemplate.queryForObject(
            "SELECT uuid, username, play_count, elo FROM users WHERE uuid = ?",
            UserRowMapper(),
            uuid
        )
    }

    override fun findUserByUsername(username: String): UserDetailOutputDTO? {
        return jdbcTemplate.queryForObject(
            "SELECT uuid, username, play_count, elo FROM users WHERE username = ?",
            UserRowMapper(),
            username
        )
    }

    override fun findUsers(offset: Int, limit: Int): List<UserDetailOutputDTO> {
        return jdbcTemplate.query(
            "SELECT uuid, username, play_count, elo FROM users LIMIT ? OFFSET ?",
            UserRowMapper(),
            limit,
            offset
        )
    }

    override fun save(username: String): Int {

        val key = GeneratedKeyHolder()

        jdbcTemplate.query(
            "SELECT uuid FROM users WHERE username = ?",
            { rs ->
                if (rs.next()) {
                    throw IllegalArgumentException("Username is already taken")
                }
            },
            username
        )

        jdbcTemplate.update({ connection ->
            val stm = connection.prepareStatement(
                "INSERT INTO users (username) VALUES (?)",
                arrayOf("uuid")
            )
            stm.setString(1, username)
            stm
        }, key)

        return key.key!!.toInt()
    }
}
