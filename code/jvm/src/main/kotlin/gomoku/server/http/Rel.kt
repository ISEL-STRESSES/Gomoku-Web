package gomoku.server.http

import gomoku.server.http.infra.LinkRelation

object Rel {

    val SELF = LinkRelation("self")
    val HOME = LinkRelation("home")
    val GAME = LinkRelation("game")
}