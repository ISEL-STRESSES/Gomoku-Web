package gomoku.server.services.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.utils.Either

/**
 * Result for joining a lobby
 * @see JoinLobbyError
 * @see Matchmaker
 */
typealias JoinLobbyResult = Either<JoinLobbyError, Matchmaker>

/**
 * Result for leaving a lobby
 * @see LeaveLobbyError
 */
typealias LeaveLobbyResult = Either<LeaveLobbyError, LeaveLobbyOutput>

/**
 * Result for getting a lobby
 * @see GetLobbyError
 * @see Lobby
 */
typealias GetLobbyResult = Either<GetLobbyError, Lobby>
