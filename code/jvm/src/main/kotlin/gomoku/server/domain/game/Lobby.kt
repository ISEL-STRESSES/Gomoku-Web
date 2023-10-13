package gomoku.server.domain.game

import gomoku.server.domain.game.player.Player
import org.apache.tomcat.util.digester.Rule

class Lobby(
    val id: Int,
    val rule: Rule,
    val players: List<Player> = emptyList()
)
