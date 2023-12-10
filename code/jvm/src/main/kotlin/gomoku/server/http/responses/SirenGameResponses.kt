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
import gomoku.server.http.controllers.game.models.RuleOutputModel
import gomoku.server.http.controllers.game.models.TurnOutput
import gomoku.server.http.controllers.lobby.models.GetLobbiesOutput
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.http.infra.ActionFieldModel
import gomoku.server.http.infra.EntityModel
import gomoku.server.http.infra.LinkModel
import gomoku.server.http.infra.PropertyDefaultModel
import gomoku.server.http.infra.SirenMediaType
import gomoku.server.http.infra.siren
import org.springframework.http.HttpMethod
import java.net.URI

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
            property(PropertyDefaultModel(body.finishedGames.size))
            body.finishedGames.forEach {
                entity(EntityModel(listOf(Rel.GAME.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Game.ROOT + "/${it.id}"))))
            }
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
            if (body.gameOutcome == null) {
                action(
                    "make-move",
                    "Make Move",
                    HttpMethod.POST,
                    URI(URIs.Game.ROOT + "/${body.id}/play"),
                    SirenMediaType,
                    listOf(
                        ActionFieldModel(name = "x", type = "number"),
                        ActionFieldModel(name = "y", type = "number")
                    )
                )
                action(
                    "get-turn",
                    "Get Turn",
                    HttpMethod.GET,
                    URI(URIs.Game.ROOT + "/${body.id}/turn"),
                    SirenMediaType,
                    emptyList()
                )
                action(
                    "forfeit-game",
                    "Forfeit Game",
                    HttpMethod.POST,
                    URI(URIs.Game.ROOT + "/${body.id}/forfeit"),
                    SirenMediaType,
                    emptyList()
                )
            }
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
            property(PropertyDefaultModel(body.rulesList.size))
            body.rulesList.forEach {
                entity(EntityModel(listOf(Rel.RULES.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Game.ROOT + "/rules/${it.ruleId}"))))
            }
            link(URIs.Game.ROOT + URIs.Game.GAME_RULES, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetRuleById {

    /**
     * TODO
     */
    fun siren(body: RuleOutputModel) =
        siren {
            clazz(Rel.RULES.value)
            property(body)
            action(
                "show-ranking",
                "Show Ranking",
                HttpMethod.GET,
                URI(URIs.Users.ROOT + "/ranking/${body.ruleId}"),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "ruleId", type = "number")
                )
            )
            link(URIs.Game.ROOT + URIs.Game.GAME_RULES + "/${body.ruleId}", Rel.SELF)
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
            if (!body.isGame){
                action(
                    "leave-lobby",
                    "Leave Lobby",
                    HttpMethod.POST,
                    URI(URIs.Lobby.ROOT + URIs.Lobby.LEAVE_LOBBY),
                    SirenMediaType,
                    listOf(
                        ActionFieldModel(name = "lobbyId", type = "number")
                    )
                )
            }
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
            property(PropertyDefaultModel(body.lobbies.size))
            body.lobbies.forEach {
                entity(EntityModel(listOf(Rel.GET_LOBBY_BY_ID.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Lobby.ROOT + "/${it.id}"))))
            }
            action(
                "create-lobby",
                "Create Lobby",
                HttpMethod.POST,
                URI(URIs.Lobby.ROOT + URIs.Lobby.CREATE_LOBBY),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "ruleId", type = "number")
                )
            )
            action(
                "join-lobby",
                "Join Lobby",
                HttpMethod.POST,
                URI(URIs.Lobby.ROOT + URIs.Lobby.JOIN_LOBBY),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "lobbyId", type = "number")
                )
            )
            action(
                "leave-lobby",
                "Leave Lobby",
                HttpMethod.POST,
                URI(URIs.Lobby.ROOT + URIs.Lobby.LEAVE_LOBBY),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "lobbyId", type = "number")
                )
            )
            action(
                "match-make",
                "Match Make",
                HttpMethod.POST,
                URI(URIs.Lobby.ROOT + URIs.Lobby.MATCH_MAKE),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "ruleId", type = "number")
                )
            )
            link(URIs.Lobby.ROOT + URIs.Lobby.GET_LOBBIES, Rel.SELF)
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
            action(
                "leave-lobby",
                "Leave Lobby",
                HttpMethod.POST,
                URI(URIs.Lobby.ROOT + URIs.Lobby.LEAVE_LOBBY),
                SirenMediaType,
                listOf(
                    ActionFieldModel(name = "lobbyId", type = "number")
                )
            )
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
            property(TurnOutput(body.turn))
            link(URIs.Game.ROOT + "/${body.gameId}/turn", Rel.SELF)
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

/**
 * TODO
 */
object GetOngoingGames {

    /**
     * TODO
     */
    fun siren(body: List<GameOutputModel>) =
        siren {
            clazz(Rel.ONGOING_GAMES.value)
            property(PropertyDefaultModel(body.size))
            body.forEach {
                entity(EntityModel(listOf(Rel.GAME.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Game.ROOT + "/${it.id}"))))
            }
            link(URIs.Game.ROOT + URIs.Game.ONGOING_GAMES, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}
