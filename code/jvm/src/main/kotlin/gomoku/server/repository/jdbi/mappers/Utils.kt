package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.GameOutcome
import gomoku.server.domain.game.game.OngoingGame
import gomoku.server.domain.game.game.move.AddMoveError
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.MoveContainer.Companion.buildMoveContainer
import gomoku.server.domain.game.rules.buildRule
import gomoku.utils.Failure
import gomoku.utils.Success
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [MoveContainer]
 * @param rs The result set to map
 * @return The [MoveContainer] from the result set
 */
fun getMoveContainerFromRS(rs: ResultSet): MoveContainer {
    val boardSize = rs.getInt("board_size")
    val moves = (rs.getArray("moves").array as Array<*>).map { it as Int }
    val moveContainerResult = buildMoveContainer(boardSize, moves)
    return when (moveContainerResult) {
        is Success -> moveContainerResult.value
        is Failure -> when (moveContainerResult.value) {
            AddMoveError.ImpossiblePosition -> throw IllegalArgumentException("Invalid position")
            AddMoveError.AlreadyOccupied -> throw IllegalArgumentException("Position already occupied")
        }
    }
}

/**
 * Maps a row of the result set to a [FinishedGame]
 * @param rs The result set to map
 * @param moveContainer The [MoveContainer] the moves of the finished game
 * @return The [FinishedGame] from the result set
 */
fun getFinishedGameFromRS(rs: ResultSet, moveContainer: MoveContainer): FinishedGame = FinishedGame(
    id = rs.getInt("id"),
    playerBlack = rs.getInt("player_black"),
    playerWhite = rs.getInt("player_white"),
    rules = buildRule(
        ruleId = rs.getInt("rules_id"),
        boardMaxSize = rs.getInt("board_size"),
        variantName = rs.getString("variant"),
        openingRuleName = rs.getString("opening_rule")
    ),
    moves = moveContainer,
    gameOutcome = GameOutcome.valueOf(rs.getString("match_outcome"))
)

/**
 * Maps a row of the result set to a [OngoingGame]
 * @param rs The result set to map
 * @param moveContainer The [MoveContainer] the moves of the ongoing game
 * @return The [OngoingGame] from the result set
 */
fun getOngoingGameFromRS(rs: ResultSet, moveContainer: MoveContainer): OngoingGame = OngoingGame(
    id = rs.getInt("id"),
    playerBlack = rs.getInt("player_black"),
    playerWhite = rs.getInt("player_white"),
    rules = buildRule(
        ruleId = rs.getInt("rules_id"),
        boardMaxSize = rs.getInt("board_size"),
        variantName = rs.getString("variant"),
        openingRuleName = rs.getString("opening_rule")
    ),
    moves = moveContainer
)
