package gomoku.server.services.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.utils.Either

typealias JoinLobbyResult = Either<JoinLobbyError, Matchmaker>

/**
 * Result for leaving a lobby
 * @see LeaveLobbyError
 */
typealias LeaveLobbyResult = Either<LeaveLobbyError, LeaveLobbyOutput>

typealias GetLobbyResult = Either<GetLobbyError, Lobby>