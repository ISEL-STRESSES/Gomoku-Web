package gomoku.server.http

import gomoku.server.http.infra.LinkRelation

/**
 * TODO
 */
object Rel {

    val SELF = LinkRelation("self")
    val NEXT = LinkRelation("next")
    val PREV = LinkRelation("prev")
    val LAST = LinkRelation("last")
    val FIRST = LinkRelation("first")

    val HOME = LinkRelation("home")

    val LOGIN = LinkRelation("login")
    val SIGNUP = LinkRelation("signup")
    val USER = LinkRelation("user")
    val USER_RANKING = LinkRelation("user-ranking")
    val USER_STATS = LinkRelation("user-stats")
    val SEARCH_RANKING = LinkRelation("search-ranking")
    val LOGOUT = LinkRelation("logout")

    val GAME = LinkRelation("game")
    val GAME_LIST = LinkRelation("game-list")
    val RULES = LinkRelation("rules")
    val MATCHMAKER = LinkRelation("matchmaker")
    val MAKE_MOVE = LinkRelation("make-move")
    val GET_TURN = LinkRelation("get-turn")
    val FORFEIT_GAME = LinkRelation("forfeit-game")
    val ONGOING_GAMES = LinkRelation("ongoing-games")

    val LEAVE_LOBBY = LinkRelation("leave-lobby")
    val JOIN_LOBBY = LinkRelation("join-lobby")
    val CREATE_LOBBY = LinkRelation("create-lobby")
    val GET_LOBBIES = LinkRelation("get-lobbies")
    val GET_LOBBY_BY_ID = LinkRelation("lobby")
}
