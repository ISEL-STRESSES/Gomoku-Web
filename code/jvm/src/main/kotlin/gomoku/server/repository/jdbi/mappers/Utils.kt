package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.AddMoveError
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.MoveContainer.Companion.buildMoveContainer
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.toMatchOutcome
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
 * Maps a row of the result set to a [FinishedMatch]
 * @param rs The result set to map
 * @param moveContainer The [MoveContainer] the moves of the finished match
 * @return The [FinishedMatch] from the result set
 */
fun getFinishedMatchFromRS(rs: ResultSet, moveContainer: MoveContainer): FinishedMatch = FinishedMatch(
    matchId = rs.getInt("id"),
    playerBlack = rs.getInt("player_black"),
    playerWhite = rs.getInt("player_white"),
    rules = buildRule(
        ruleId = rs.getInt("rules_id"),
        boardMaxSize = rs.getInt("board_size"),
        variantName = rs.getString("variant"),
        openingRuleName = rs.getString("opening_rule")
    ),
    moves = moveContainer,
    matchOutcome = rs.getString("match_outcome").toMatchOutcome()
)

/**
 * Maps a row of the result set to a [OngoingMatch]
 * @param rs The result set to map
 * @param moveContainer The [MoveContainer] the moves of the ongoing match
 * @return The [OngoingMatch] from the result set
 */
fun getOngoingMatchFromRS(rs: ResultSet, moveContainer: MoveContainer): OngoingMatch = OngoingMatch(
    matchId = rs.getInt("id"),
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
