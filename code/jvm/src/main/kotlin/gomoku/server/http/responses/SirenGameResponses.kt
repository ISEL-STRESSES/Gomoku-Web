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
 * Represents the retrieval and display of finished games.
 */
object GetFinishedGames {

    /**
     * Generates a Siren response for displaying finished games.
     * @param body The GetFinishedGamesOutputModel containing the finished games.
     * @param totalPages Total number of pages available for pagination.
     * @param currentOffset Current page offset in the pagination.
     * @param currentLimit Maximum number of items per page.
     * @return Siren response for the finished games list.
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
 * Represents the retrieval of a specific game by its ID.
 */
object GetGameById {

    /**
     * Creates a Siren response for a specific game's details.
     * @param body The GameOutputModel containing detailed information about the game.
     * @return Siren response for the specific game.
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
 * Represents the retrieval of game rules.
 */
object GetRules {

    /**
     * Generates a Siren response for listing game rules.
     * @param body The GetRulesOutputModel containing the list of game rules.
     * @return Siren response for the rules list.
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

/**
 * Represents the retrieval of a specific rule by its ID.
 */
object GetRuleById {

    /**
     * Creates a Siren response for a specific rule's details.
     * @param body The RuleOutputModel containing detailed information about the rule.
     * @return Siren response for the specific rule.
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
 * Represents the matchmaker functionality for game lobby management.
 */
object Matchmaker {

    /**
     * Generates a Siren response for the matchmaker output.
     * @param body The MatchmakerOutputModel containing matchmaker information.
     * @return Siren response for the matchmaker.
     */
    fun siren(body: MatchmakerOutputModel) =
        siren {
            clazz(Rel.MATCHMAKER.value)
            property(body)
            if (!body.isGame) {
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
 * Represents the functionality for a user to leave a game lobby.
 */
object LeaveLobby {

    /**
     * Creates a Siren response for leaving a lobby.
     * @param body The LeaveLobbyOutput containing information about the lobby left.
     * @return Siren response for leaving the lobby.
     */
    fun siren(body: LeaveLobbyOutput) =
        siren {
            clazz(Rel.LEAVE_LOBBY.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * Represents the functionality for a user to join a game lobby.
 */
object JoinLobby {

    /**
     * Generates a Siren response for joining a lobby.
     * @param body The Matchmaker object representing the lobby to join.
     * @return Siren response for joining the lobby.
     */
    fun siren(body: Matchmaker) =
        siren {
            clazz(Rel.JOIN_LOBBY.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * Represents the retrieval of available game lobbies.
 */
object GetLobbies {

    /**
     * Creates a Siren response for listing available lobbies.
     * @param body The GetLobbiesOutput containing the list of available lobbies.
     * @return Siren response for the lobbies list.
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
 * Represents the functionality for creating a game lobby.
 */
object CreateLobby {

    /**
     * Generates a Siren response for creating a lobby.
     * @param body The Matchmaker object representing the new lobby.
     * @return Siren response for creating the lobby.
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
 * Represents the retrieval of a specific lobby by its ID.
 */
object GetLobbyById {

    /**
     * Creates a Siren response for a specific lobby's details.
     * @param body The Lobby object containing detailed information about the lobby.
     * @return Siren response for the specific lobby.
     */
    fun siren(body: Lobby) =
        siren {
            clazz(Rel.GET_LOBBY_BY_ID.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * Represents the functionality for making a move in a game.
 */
object MakeMove {

    /**
     * Generates a Siren response for making a move in a game.
     * @param body The GameOutputModel representing the state of the game after the move.
     * @return Siren response for the game state post-move.
     */
    fun siren(body: GameOutputModel) =
        siren {
            clazz(Rel.MAKE_MOVE.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * Represents the retrieval of the current turn in a game.
 */
object GetTurn {

    /**
     * Creates a Siren response for the current turn in a game.
     * @param body The CurrentTurnPlayerOutput indicating who has the current turn.
     * @return Siren response for the current turn.
     */
    fun siren(body: CurrentTurnPlayerOutput) =
        siren {
            clazz(Rel.GET_TURN.value)
            property(TurnOutput(body.turn))
            link(URIs.Game.ROOT + "/${body.gameId}/turn", Rel.SELF)
        }
}

/**
 * Represents the functionality for a user to forfeit a game.
 */
object ForfeitGame {

    /**
     * Generates a Siren response for forfeiting a game.
     * @param body The GameOutputModel representing the state of the game after forfeit.
     * @return Siren response for the game state post-forfeit.
     */
    fun siren(body: GameOutputModel) =
        siren {
            clazz(Rel.FORFEIT_GAME.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * Represents the retrieval of ongoing games.
 */
object GetOngoingGames {

    /**
     * Creates a Siren response for listing ongoing games.
     * @param body The list of GameOutputModel representing the ongoing games.
     * @return Siren response for the list of ongoing games.
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
