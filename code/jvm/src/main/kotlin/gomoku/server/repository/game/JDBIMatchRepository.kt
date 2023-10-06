package gomoku.server.repository.game

import gomoku.server.domain.game.Rules
import gomoku.server.domain.game.board.Position
import org.jdbi.v3.core.Handle

class JDBIMatchRepository(private val handle: Handle) : MatchRepository {

    private fun createWaitingLobby(rules: Rules): Int =
        handle.createUpdate("insert into lobby (rules_id) values (:rules)")
            .bind("rules", rules)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

    override fun createGameRules(rules: Rules): Int =
        handle.createUpdate("insert into rules (board_size, opening_rule, variant) values (:board_size, :opening_rule, :variant)")
            .bind("rules", rules.boardSize)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

    override fun joinWaitingLobby(rules: Rules): Int =
        handle.createQuery("select id from lobby where rules_id = (select id from rules where board_size = :rules)")
            .bind("rules", rules.boardSize)
            .mapTo(Int::class.java)
            .singleOrNull() ?: createWaitingLobby(rules)

    override fun initiateGame(playerAId: Int, playerBId: Int): Int {
        val lobbyId = handle.createQuery("select id from lobby where (select lobby_id from enters_lobby where user_id = :playerAId) = (select lobby_id from enters_lobby where user_id = :playerBId)")
            .mapTo(Int::class.java)
            .singleOrNull() ?: throw IllegalStateException("Players are not in the same lobby")
        val isPlayerABlack = Math.random() < 0.5
        return handle.createUpdate(
            """
            insert into matches(player_a_id, player_b_id, is_player_a_black, turn, moves, lobby_id ) 
            values (:playerAId, :playerBId, :isPlayerABlack,:turn, :moves, :lobbyId)
            """.trimIndent()
        )
            .bind("playerAId", playerAId)
            .bind("playerBId", playerBId)
            .bind("isPlayerABlack", isPlayerABlack)
            .bind("turn", if (isPlayerABlack) playerAId else playerBId)
            .bind("moves", "")
            .bind("lobbyId", lobbyId)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
    }

    override fun getWinner(gameId: Int): String? = // TODO: find a way to differ between draw and unfinished game
        handle.createQuery("select winner from matches where id = :gameId")
            .bind("gameId", gameId)
            .mapTo(String::class.java)
            .singleOrNull()

    override fun getMoves(gameId: Int): List<Position>? =
        handle.createQuery("select moves from matches where id = :gameId")
            .bind("gameId", gameId)
            .mapTo(Array<String>::class.java)
            .singleOrNull()
            ?.map { Position.fromString(it) }

    override fun makeMove(gameId: Int, position: Position, matchId: Int) {
        val movesDone: List<Position> = handle.createQuery("select moves from matches where id = :gameId")
            .bind("gameId", gameId)
            .mapTo(Array<String>::class.java)
            .singleOrNull()
            ?.map { Position.fromString(it) } ?: emptyList<Position>()
        if (position !in movesDone) {
            handle.createUpdate("update matches set moves = :moves where id = :gameId")
                .bind("gameId", gameId)
                .bind("moves", movesDone.plus(position))
                .execute()
        } else {
            throw IllegalStateException("Position Occupied")
        }
    }

    override fun getGameState(gameId: Int) {
        TODO("Not yet implemented")
    }

    override fun getTurn(gameId: Int): Int? =
        handle.createQuery("select turn from matches where id = :gameId")
            .bind("gameId", gameId)
            .mapTo(Int::class.java)
            .singleOrNull()
}
