package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.services.errors.MatchmakingError
import gomoku.utils.Either

typealias MatchMakingResult = Either<MatchmakingError, Matchmaker>