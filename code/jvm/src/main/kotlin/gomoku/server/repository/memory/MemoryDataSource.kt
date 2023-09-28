package gomoku.server.repository.memory

import gomoku.server.domain.game.Game

data class UserDAO(
    val uuid: Int,
    val username: String,
    val elo: Int,
    val gamesPlayed: Int,
    val password: String,
)

data class MemoryDataSource (
    val users: MutableMap<Int, UserDAO> = mutableMapOf(),
    val games: MutableMap<Int, Game> = mutableMapOf()
)