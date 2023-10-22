package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.GetMatchError
import gomoku.server.services.errors.game.LeaveLobbyError
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.utils.Either

/**
 * Result for matchmaking
 * @see MatchmakingError
 * @see Matchmaker
 */
typealias MatchmakingResult = Either<MatchmakingError, Matchmaker>

/**
 * Result for making a move
 * @see MakeMoveError
 * @see Match
 */
typealias MakeMoveResult = Either<MakeMoveError, Match>

/**
 * Result for getting the current turn player
 * @see CurrentTurnPlayerError
 * @see Match
 */
typealias CurrentTurnPlayerResult = Either<CurrentTurnPlayerError, Int>

typealias LeaveLobbyResult = Either<LeaveLobbyError, Unit>

/**
 * Result for getting a match
 * @see GetMatchError
 */
typealias GetMatchResult = Either<GetMatchError, Match>
