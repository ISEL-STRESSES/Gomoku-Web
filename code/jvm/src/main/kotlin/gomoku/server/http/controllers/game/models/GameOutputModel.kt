package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.Game
import gomoku.server.domain.game.game.OngoingGame
import gomoku.server.domain.game.game.Turn

/**
 * Represents the output model of a game to be sent from the API
 * @param id the id of the game
 * @param playerBlack the id of the black player
 * @param playerWhite the id of the white player
 * @param rule the rules of the game
 * @param moves the moves of the game
 * @param gameOutcome the outcome of the game
 * @param turn the color of the player whose turn it is
 * @param type the type of the game
 */
data class GameOutputModel(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val rule: RuleOutputModel,
    val moves: MoveContainerOutputModel,
    val gameOutcome: String?,
    val turn: Turn?,
    val type: GameType
) {

    constructor(finishedGame: FinishedGame) : this(
        id = finishedGame.id,
        playerBlack = finishedGame.playerBlack,
        playerWhite = finishedGame.playerWhite,
        rule = RuleOutputModel(finishedGame.rules),
        moves = MoveContainerOutputModel(finishedGame.moveContainer),
        gameOutcome = finishedGame.gameOutcome.name,
        turn = null,
        type = GameType.FINISHED
    )

    constructor(ongoingGame: OngoingGame) : this(
        id = ongoingGame.id,
        playerBlack = ongoingGame.playerBlack,
        playerWhite = ongoingGame.playerWhite,
        rule = RuleOutputModel(ongoingGame.rules),
        moves = MoveContainerOutputModel(ongoingGame.moveContainer),
        gameOutcome = null,
        turn = ongoingGame.turn,
        type = GameType.ONGOING
    )

    companion object {
        /**
         * Resolves the representation of a game to be sent from the API
         * @param game the game to be resolved
         * @return the representation of the game
         */
        fun fromGame(game: Game): GameOutputModel {
            return when (game) {
                is OngoingGame -> GameOutputModel(game)
                is FinishedGame -> GameOutputModel(game)
            }
        }
    }

    /**
     * Represents the type of game either ongoing or finished
     */
    enum class GameType {
        ONGOING, FINISHED
    }
}
