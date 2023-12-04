package gomoku.server.http.responses

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.controllers.game.models.CurrentTurnPlayerOutput
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.controllers.game.models.GetFinishedGamesOutputModel
import gomoku.server.http.controllers.game.models.GetRulesOutputModel
import gomoku.server.http.controllers.game.models.MatchmakerOutputModel
import gomoku.server.http.controllers.lobby.models.GetLobbiesOutput
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.http.infra.siren

/**
 * TODO
 */
object GetFinishedGames {

    /**
     * TODO
     */
    fun siren(body: GetFinishedGamesOutputModel, totalPages: Int, currentOffset: Int, currentLimit: Int) =
        siren {
            clazz(Rel.GAME_LIST.value)
            property(body)
            link(URIs.Game.ROOT + URIs.Game.HUB, Rel.SELF)
            link(URIs.HOME, Rel.HOME)

            if (currentOffset + currentLimit < totalPages * currentLimit) { // If we are not on the last page
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=${currentOffset + currentLimit}",
                    Rel.NEXT
                )
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=${(totalPages - 1) * currentLimit}",
                    Rel.LAST
                )
            }

            if (currentOffset > 0) { // If we are not on the first page
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=${if (currentOffset - currentLimit < 0) 0 else currentOffset - currentLimit}",
                    Rel.PREV
                )
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=0",
                    Rel.FIRST
                )
            }
        }
}

/**
 * TODO
 */
object GetGameById {

    /**
     * TODO
     */
    fun siren(body: GameOutputModel) =
        siren {
            clazz(Rel.GAME.value)
            property(body)
            link(URIs.Game.ROOT + "/${body.id}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetRules {

    /**
     * TODO
     */
    fun siren(body: GetRulesOutputModel) =
        siren {
            clazz(Rel.RULES.value)
            property(body)
            link(URIs.Game.ROOT + URIs.Game.GAME_RULES, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object Matchmaker {

    /**
     * TODO
     */
    fun siren(body: MatchmakerOutputModel) =
        siren {
            clazz(Rel.MATCHMAKER.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object LeaveLobby {

    /**
     * TODO
     */
    fun siren(body: LeaveLobbyOutput) =
        siren {
            clazz(Rel.LEAVE_LOBBY.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object JoinLobby {

    /**
     * TODO
     */
    fun siren(body: Matchmaker) =
        siren {
            clazz(Rel.JOIN_LOBBY.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetLobbies {

    /**
     * TODO
     */
    fun siren(body: GetLobbiesOutput) =
        siren {
            clazz(Rel.GET_LOBBIES.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object CreateLobby {

    /**
     * TODO
     */
    fun siren(body: Matchmaker) =
        siren {
            clazz(Rel.CREATE_LOBBY.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetLobbyById {

    /**
     * TODO
     */
    fun siren(body: Lobby) =
        siren {
            clazz(Rel.GET_LOBBY_BY_ID.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object MakeMove {

    /**
     * TODO
     */
    fun siren(body: GameOutputModel) =
        siren {
            clazz(Rel.MAKE_MOVE.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetTurn {

    /**
     * TODO
     */
    fun siren(body: CurrentTurnPlayerOutput) =
        siren {
            clazz(Rel.GET_TURN.value)
            property(body)
            link(URIs.Game.ROOT + "/${body.turn}/turn", Rel.SELF)
        }
}

/**
 * TODO
 */
object ForfeitGame {

    /**
     * TODO
     */
    fun siren(body: GameOutputModel) =
        siren {
            clazz(Rel.FORFEIT_GAME.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}
