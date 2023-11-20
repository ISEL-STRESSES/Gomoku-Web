package gomoku.server.http

import gomoku.server.http.infra.LinkRelation

object Rel {

    val SELF = LinkRelation("self")
    val NEXT = LinkRelation("next")
    val PREV = LinkRelation("previous")
    val LAST = LinkRelation("last")
    val FIRST = LinkRelation("first")
    val HOME = LinkRelation("home")
    val GAME = LinkRelation("game")
}
