package gomoku.server.domain.user

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EloTest {

    @Test
    fun `playerA wins and has the same base points as playerB`() {
        val pointsA = 1500.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.WIN) > updateElo(pointsA, pointsB, RankingUserData.LOSS))
    }

    @Test
    fun `playerB ties and has the same base points as playerB`() {
        val pointsA = 1500.0
        val pointsB = 1500.0
        assertEquals(
            updateElo(pointsA, pointsB, RankingUserData.DRAW),
            updateElo(pointsB, pointsA, RankingUserData.DRAW)
        )
    }

    @Test
    fun `playerA wins and has higher base points than playerB`() {
        val pointsA = 1600.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.WIN) > pointsA)
    }

    @Test
    fun `playerA wins and has lower base points than playerB`() {
        val pointsA = 1400.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.WIN) > pointsA)
    }

    @Test
    fun `playerA loses and has the same base points as playerB`() {
        val pointsA = 1500.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.LOSS) < pointsA)
    }

    @Test
    fun `playerA loses and has higher base points than playerB`() {
        val pointsA = 1600.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.LOSS) < pointsA)
    }

    @Test
    fun `playerA loses and has lower base points than playerB`() {
        val pointsA = 1400.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, RankingUserData.LOSS) < pointsA)
    }
}
