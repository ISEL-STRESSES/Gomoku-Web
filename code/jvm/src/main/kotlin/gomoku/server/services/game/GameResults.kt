package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
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
