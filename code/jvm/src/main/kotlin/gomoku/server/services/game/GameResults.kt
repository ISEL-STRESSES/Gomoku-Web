package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchNotFoundError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.utils.Either

typealias MatchmakingResult = Either<MatchmakingError, Matchmaker>

typealias MakeMoveResult = Either<MakeMoveError, Match>

typealias GetMatchResult = Either<MatchNotFoundError, Match>
