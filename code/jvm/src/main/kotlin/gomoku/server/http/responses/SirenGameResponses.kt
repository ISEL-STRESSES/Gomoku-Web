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
    fun siren(body: GetFinishedGamesOutputModel) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + URIs.Game.HUB, Rel.SELF)
        }
}

object GetGameById {
    fun siren(body: GameOutputModel) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + "/${body.id}", Rel.SELF)
        }
}

object GetRules {
    fun siren(body: GetRulesOutputModel) =
        siren(body) {
            clazz("rules")
            link(URIs.Game.ROOT + URIs.Game.GAME_RULES, Rel.SELF)
        }
}

object Matchmaker {
    fun siren(body: MatchmakerOutputModel) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + "/${body.id}", Rel.SELF)
        }
}

object LeaveLobby {
    fun siren(body: LeaveLobbyOutput) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + "/${body.lobbyId}/leave", Rel.SELF)
        }
}

object MakeMove {
    fun siren(body: GameOutputModel) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + "/${body.id}/play", Rel.SELF)
        }
}

object GetTurn {
    fun siren(body: CurrentTurnPlayerOutput) =
        siren(body) {
            clazz("game")
            link(URIs.Game.ROOT + "/${body.turn}/turn", Rel.SELF)
        }
}