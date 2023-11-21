package gomoku.server.repository.game

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.Game
import gomoku.server.domain.game.game.GameOutcome
import gomoku.server.domain.game.game.GameState
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.toColor
import gomoku.server.domain.game.rules.Rules
import gomoku.server.repository.jdbi.mappers.game.MovesRowMapper
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Repository for matches using JDBI and PostgresSQL
 * @property handle The handle to the database
 * @see GameRepository
 */
class JDBIGameRepository(private val handle: Handle) : GameRepository {

    // rules
    /**
     * Gets a rule by its id.
     * @param ruleId id of the rule
     * @return the rule or null if not found
     */
    override fun getRuleById(ruleId: Int): Rules? =
        handle.createQuery("select * from rules where id = :ruleId")
            .bind("ruleId", ruleId)
            .mapTo<Rules>()
            .singleOrNull()

    /**
     * Gets all the rules.
     * @return list of rules
     */
    override fun getAllRules(): List<Rules> =
        handle.createQuery("select * from rules")
            .mapTo<Rules>()
            .list()

    /**
     * Checks if a rule with the given id is stored in the database.
     * @param ruleId id of the rule
     * @return true if the rule is stored, false otherwise
     */
    override fun isRuleStoredById(ruleId: Int): Boolean =
        handle.createQuery(
            """
        SELECT EXISTS (
            SELECT 1 FROM rules WHERE id = :ruleId
        )
    """
        )
            .bind("ruleId", ruleId)
            .mapTo<Boolean>()
            .single()

    // game
    /**
     * Verifies if the game is stored based on the [gameId]
     * @param gameId id of the game
     * @return true if the game is stored, false otherwise
     */
    override fun isGameStoredById(gameId: Int): Boolean =
        handle.createQuery(
            """
                SELECT EXISTS (
                    SELECT 1 FROM matches WHERE id = :matchId
                )
        """
        )
            .bind("matchId", gameId)
            .mapTo<Boolean>()
            .single()

    /**
     * Creates a new game, with the given rule and users ids
     * setting the game state to [GameState.ONGOING]
     * @param ruleId id of the rule
     * @param playerBlackId id of the player playing with black stones
     * @param playerWhiteId id of the player playing with white stones
     * @return id of the game
     */
    override fun createGame(ruleId: Int, playerBlackId: Int, playerWhiteId: Int): Int =
        handle.createUpdate(
            """
            insert into matches(rules_id, match_state, player_black, player_white)
            values (:ruleId, :matchState, :playerBlackId, :playerWhiteId)
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("matchState", GameState.ONGOING.name)
            .bind("playerBlackId", playerBlackId)
            .bind("playerWhiteId", playerWhiteId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    /**
     * Gets the game by its id.
     * @param gameId id of the game
     * @return the game or null if not found
     */
    override fun getGameById(gameId: Int): Game? =
        handle.createQuery(
            """
            select matches.id , matches.player_black, matches.player_white, matches.match_state, matches.match_outcome, matches.moves,
            rules.id as rules_id,rules.board_size, rules.opening_rule, rules.variant
            from matches join rules
            on rules.id = matches.rules_id
            where matches.id = :matchId
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .mapTo<Game>()
            .singleOrNull()

    override fun getUserFinishedGames(offset: Int, limit: Int, userId: Int): List<FinishedGame> =
        handle.createQuery(
            """
                select matches.id, matches.player_black, matches.player_white, matches.match_state, matches.match_outcome, matches.moves,
                rules.id as rules_id, rules.board_size, rules.opening_rule, rules.variant
                from matches join rules
                on rules.id = matches.rules_id
                where (matches.player_black = :userId or matches.player_white = :userId) and matches.match_state = 'FINISHED'
                order by matches.id desc
                limit :limit offset :offset
            """.trimIndent()
        )
            .bind("userId", userId)
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo<FinishedGame>()
            .list()

    /**
     * Gets the number of finished games a user has.
     * @param userId the id of the user
     * @return the number of finished games
     */
    override fun getUserFinishedGamesCount(userId: Int): Int {
        return handle.createQuery(
            """
                select count(*) from matches
                where (matches.player_black = :userId or matches.player_white = :userId) and matches.match_state = 'FINISHED'
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .single()
    }

    /**
     * Gets the state of the game.
     * @param gameId id of the game
     * @return state of the game or null if the game doesn't exist
     */
    override fun getGameState(gameId: Int): GameState? =
        handle.createQuery("select match_state from matches where id = :matchId")
            .bind("matchId", gameId)
            .mapTo<GameState>()
            .singleOrNull()

    /**
     * Sets the state of the game.
     * @param gameId id of the game
     * @param state the new state of the game
     */
    override fun setGameState(gameId: Int, state: GameState) {
        handle.createUpdate(
            """
            update matches set match_state = :state where id = :matchId
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .bind("state", state.name)
            .execute()
    }

    /**
     * Gets the winner of the game.
     * @param gameId id of the game
     * @return id of the winner, draw or null if the game is not finished
     */
    override fun getGameOutcome(gameId: Int): GameOutcome? =
        handle.createQuery("select match_outcome from matches where id = :matchId and match_state = 'FINISHED'")
            .bind("matchId", gameId)
            .mapTo<GameOutcome>()
            .singleOrNull()

    /**
     * Sets the [GameOutcome] of the game.
     * @param gameId id of the game
     * @param outcome outcome of the game
     */
    override fun setGameOutcome(gameId: Int, outcome: GameOutcome) {
        handle.createUpdate(
            """
            update matches set match_outcome = :outcome where id = :matchId and match_state = 'FINISHED'
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .bind("outcome", outcome.name)
            .execute()
    }

    /**
     * Gets the rule of the game.
     * @param gameId id of the game
     * @return the rule or null if the game doesn't exist
     */
    override fun getGameRule(gameId: Int): Rules? =
        handle.createQuery(
            """
            select id, board_size, variant, opening_rule from rules where rules.id = (
            select rules_id from matches where matches.id = :matchId
            )
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .mapTo<Rules>()
            .singleOrNull()

    /**
     * Gets the players of a game.
     * @param gameId id of the game
     * @return pair of players
     */
    override fun getGamePlayers(gameId: Int): GamePlayers? =
        handle.createQuery(
            """
            select player_black, player_white from matches where id = :matchId
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .map { rs, _ ->
                Pair(rs.getInt("player_black"), rs.getInt("player_white"))
            }
            .firstOrNull()

    /**
     * Makes a move in the game.
     * @param gameId id of the game
     * @param index index of the move
     * @return true if the move was added, false otherwise
     */
    override fun addToMoveArray(gameId: Int, index: Int): Boolean {
        val update = handle.createUpdate(
            """
            update matches set moves = array_append(matches.moves, :index) where id = :matchId and match_state = 'ONGOING'
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .bind("index", index)
            .execute()

        return update == 1
    }

    /**
     * Gets the moves of the game.
     * @param gameId id of the game
     * @return list of moves
     */
    override fun getAllMoves(gameId: Int): List<Move> =
        handle.createQuery(
            """
            select m.moves, r.board_size from matches m join rules r on r.id = m.rules_id where m.id = :matchId
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .map(MovesRowMapper())
            .one()

    /**
     * Gets the last n moves of the game.
     * @param gameId id of the game
     * @param n number of moves to get
     * @return list of moves
     */
    override fun getLastNMoves(gameId: Int, n: Int): List<Move> =
        getAllMoves(gameId).takeLast(n)

    /**
     * Gets the turn of the game.
     * @param gameId id of the game
     * @return the color of the player whose turn it is, of null if
     * the game has already ended or doesn't exist.
     */
    override fun getTurn(gameId: Int): CellColor? =
        handle.createQuery(
            """
            SELECT COALESCE(array_length(matches.moves, 1), 0) as array_length
            FROM matches
            WHERE id = :matchId AND match_state = 'ONGOING';
            """.trimIndent()
        )
            .bind("matchId", gameId)
            .mapTo<Int>()
            .singleOrNull()?.toColor()
}
