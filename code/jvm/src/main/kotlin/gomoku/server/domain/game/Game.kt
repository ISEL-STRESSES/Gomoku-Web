package model

import model.board.*
import kotlin.random.Random

sealed class Game (
    val gameID: Int,
    val hostID: Int,
    val rules: Rules,
)

/**
 * Represents an open game that can be joined by a guest.
 */
class OpenGame (
    gameID: Int,
    hostID: Int,
    rules: Rules = defaultRules,
) : Game(gameID, hostID, rules)

class OngoingGame(
    gameID: Int,
    hostID: Int,
    rules: Rules,
    val guestID: Int,
    val moves: SerializedMoves,
    val isHostBlack: Boolean,
) : Game(gameID, hostID, rules)

fun OpenGame.join(guestID : Int): OngoingGame {
    val isHostBlack = Random.nextBoolean()

    return OngoingGame(
        gameID = gameID,
        hostID = hostID,
        guestID = guestID,
        rules = rules,
        moves = emptyList(),
        isHostBlack = isHostBlack,
    )
}

fun Game.getColorFromPlayerID(playerID: Int): Color? {
    return when(this) {
        is OpenGame -> null
        is OngoingGame -> {
            if (playerID == hostID) {
                if (isHostBlack) Color.BLACK else Color.WHITE
            } else if (playerID == guestID) {
                if (isHostBlack) Color.WHITE else Color.BLACK
            } else {
                null
            }
        }
    }
}

fun OngoingGame.play(playerID: Int, position: Position): Result<Game> {
    if (playerID != hostID || playerID != guestID)
        return Result.failure(THIS_AINT_YO_GAME_Exception())

    if (!rules.boardSize.isPositionInside(position))
        return Result.failure(OutOfBoundsException())

    val currentColor = moves.nextMoveColor()
    if (getColorFromPlayerID(playerID) != currentColor)
        return Result.failure(NotYourTurnException())

    // Construct lookup board
    val board = moves.toBoard()
    board.at(position)?.let {
        return Result.failure(IllegalMoveException())
    }

    // TODO: is the playing move a winning move?

    val newMoves = moves + position;

    val newGame = OngoingGame(
        gameID = gameID,
        hostID = hostID,
        guestID = guestID,
        rules = rules,
        moves = newMoves,
        isHostBlack = isHostBlack,
    )

    return Result.success(
        newGame
    )
}

