package gomoku.server.http.responses

import gomoku.server.domain.game.CurrentTurnPlayerOutput
import gomoku.server.domain.game.LeaveLobbyOutput
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.controllers.game.models.GetFinishedGamesOutputModel
import gomoku.server.http.controllers.game.models.GetRulesOutputModel
import gomoku.server.http.controllers.game.models.MatchmakerOutputModel
import gomoku.server.http.infra.siren

object GetFinishedGames {
    fun siren(body: GetFinishedGamesOutputModel, totalPages: Int, currentOffset: Int, currentLimit: Int) =
        siren(body) {
            clazz(Rel.GAME_LIST.value)
            link(URIs.Game.ROOT + URIs.Game.HUB, Rel.SELF)
            link(URIs.HOME, Rel.HOME)

            if (currentOffset + currentLimit < totalPages * currentLimit) { //If we are not on the last page
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=${currentOffset + currentLimit}",
                    Rel.NEXT
                )
                link(
                    URIs.Game.ROOT + URIs.Game.HUB + "?limit=$currentLimit&offset=${(totalPages - 1) * currentLimit}",
                    Rel.LAST
                )
            }

            if (currentOffset > 0) { //If we are not on the first page
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

object GetGameById {
    fun siren(body: GameOutputModel) =
        siren(body) {
            clazz(Rel.GAME.value)
            link(URIs.Game.ROOT + "/${body.id}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetRules {
    fun siren(body: GetRulesOutputModel) =
        siren(body) {
            clazz(Rel.RULES.value)
            link(URIs.Game.ROOT + URIs.Game.GAME_RULES, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object Matchmaker {
    fun siren(body: MatchmakerOutputModel) =
        siren(body) {
            clazz(Rel.MATCHMAKER.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object LeaveLobby {
    fun siren(body: LeaveLobbyOutput) =
        siren(body) {
            clazz(Rel.LEAVE_LOBBY.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object MakeMove {
    fun siren(body: GameOutputModel) =
        siren(body) {
            clazz(Rel.MAKE_MOVE.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetTurn {
    fun siren(body: CurrentTurnPlayerOutput) =
        siren(body) {
            clazz(Rel.GET_TURN.value)
            link(URIs.Game.ROOT + "/${body.turn}/turn", Rel.SELF)
        }
}
