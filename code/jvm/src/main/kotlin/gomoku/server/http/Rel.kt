package gomoku.server.http

import gomoku.server.http.infra.LinkRelation

object Rel {

    val SELF = LinkRelation("self")
    val NEXT = LinkRelation("next")
    val PREVIOUS = LinkRelation("previous")
    val HOME = LinkRelation("home")
    val GAME = LinkRelation("game")
}