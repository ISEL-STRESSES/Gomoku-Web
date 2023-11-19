package gomoku.server.domain.game

/**
 * Represents the result of a start matchmaking request
 * @property isGame if the matchmaking was successful, meaning that a game was created,
 * or not successful, meaning that the user was added to the lobby queue
 * @property id the id of the game or lobby the user was added to
 */
data class Matchmaker(
    val isGame: Boolean,
    val id: Int
)

data class LeaveLobbyOutput(
    val lobbyId: Int,
    val userId: Int
)

data class CurrentTurnPlayerOutput(
    val turn: Int
)