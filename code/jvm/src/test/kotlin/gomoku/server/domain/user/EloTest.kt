package gomoku.server.domain.user

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class EloTest {
    @Test
    fun `playerA wins and has the same base points as playerB`() {
        val pointsA = 1500.0
        val pointsB = 1500.0
        assertTrue(updateElo(pointsA, pointsB, 1.0) > pointsA)

        println("New rating for player A after a win: ${updateElo(pointsA, pointsB, 1.0)}")

//        // Reset rating for player A for next example
//        rA = 1500.0
//
//        // Update rating for player A after a draw
//        sA = 0.5
//        rA = updateElo(rA, rB, sA)
//        println("New rating for player A after a draw: $rA")
//
//        // Reset rating for player A for next example
//        rA = 1500.0
//
//        // Update rating for player A after a loss
//        sA = 0.0
//        rA = updateElo(rA, rB, sA)
//        println("New rating for player A after a loss: $rA")

        /*
        k = 32
        New rating for player A after a win: 1520.4820799936924
        New rating for player A after a draw: 1504.4820799936924
        New rating for player A after a loss: 1488.4820799936924
         */

        /*
        k = 40
        New rating for player A after a win: 1525.6025999921153
        New rating for player A after a draw: 1505.6025999921153
        New rating for player A after a loss: 1485.6025999921153
        */
    }
}