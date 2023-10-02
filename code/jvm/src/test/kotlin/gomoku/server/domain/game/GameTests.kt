package gomoku.server.domain.game

import gomoku.server.domain.game.board.Position
import org.junit.jupiter.api.Assertions.assertEquals

const val GAME_ID = 0
const val HOST_ID = 1000
const val GUEST_ID = 2000

class GameTests {

    fun `Test joining a Game`() {
        val start = OpenGame(GAME_ID, HOST_ID)
        val ongoing = start.join(GUEST_ID).getOrThrow()
        assertEquals(GAME_ID, ongoing.gameID)
        assertEquals(HOST_ID, ongoing.hostID)
        assertEquals(GUEST_ID, ongoing.guestID)
        assert(ongoing.moves.isEmpty())
    }

    fun `Try Playing a game`() {
        val ongoingGame = OngoingGame(
            gameID = GAME_ID,
            hostID = HOST_ID,
            guestID = GUEST_ID,
            rules = defaultRules,
            moves = emptyList(),
            isHostBlack = true
        )

        val position = Position(0, 0)
        val gameAfterMove = ongoingGame.play(HOST_ID, position).getOrThrow()
        check(gameAfterMove is OngoingGame)
        assert(gameAfterMove.moves.size == 1)
        assertEquals(position, gameAfterMove.moves[0])
    }
}
