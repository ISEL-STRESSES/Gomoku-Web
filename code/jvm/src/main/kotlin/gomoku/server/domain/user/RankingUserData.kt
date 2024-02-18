package gomoku.server.domain.user

import gomoku.server.domain.user.RankingUserData.Companion.K_FACTOR
import kotlin.math.pow

/**
 * Represents the data of a user for the ranking system.
 * @property uuid The uuid of the user.
 * @property username The username of the user.
 * @property ruleId The id of the rule the user is playing.
 * @property rank The rank of the user.
 * @property gamesPlayed The number of games played by the user.
 * @property elo The elo of the user.
 */
data class RankingUserData(
    val uuid: Int,
    val username: String,
    val ruleId: Int,
    val rank: Int = 0,
    val gamesPlayed: Int = 0,
    val elo: Int = DEFAULT_ELO
) {
    init {
        require(gamesPlayed >= 0) { "gamesPlayed must be positive" }
        require(elo in 0..4000) { "elo must be between 0 and 4000" }
    }

    companion object {
        const val K_FACTOR = 40.0
        const val DEFAULT_ELO = 1500
        const val WIN = 1.0
        const val DRAW = 0.5
        const val LOSS = 0.0
    }
}

/**
 * Computes the expected score for player A based on the ratings of both player A and B.
 * @param ratingPA Rating of player A.
 * @param ratingPB Rating of player B.
 * @return Expected score for player A.
 */
fun expectedScore(ratingPA: Double, ratingPB: Double): Double {
    return 1.0 / (1.0 + 10.0.pow((ratingPB - ratingPA) / 400.0))
}

/**
 * Updates and returns the new Elo rating for player A after a game.
 * @param ratingPA Current rating of player A.
 * @param ratingPB Rating of player B.
 * @param playerAScore Actual score of player A (1 for win, 0.5 for draw, 0 for loss).
 * @return New rating for player A.
 */
fun updateElo(ratingPA: Double, ratingPB: Double, playerAScore: Double): Double {
    val expectedScore = expectedScore(ratingPA, ratingPB)
    return ratingPA + K_FACTOR * (playerAScore - expectedScore)
}
