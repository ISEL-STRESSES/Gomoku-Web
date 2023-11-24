package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.Game
import gomoku.server.http.controllers.game.models.CurrentTurnPlayerOutput
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.ForfeitGameError
import gomoku.server.services.errors.game.GetGameError
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
 * @see Game
 */
typealias MakeMoveResult = Either<MakeMoveError, Game>

/**
 * Result for getting the current turn player
 * @see CurrentTurnPlayerError
 * @see Game
 */
typealias CurrentTurnPlayerResult = Either<CurrentTurnPlayerError, CurrentTurnPlayerOutput>

/**
 * Result for getting a game
 * @see GetGameError
 */
typealias GetGameResult = Either<GetGameError, Game>

typealias GetUserFinishedGamesResult = Pair<List<FinishedGame>, Int>

/**
 * Result for forfeiting a game
 * @see ForfeitGameError
 * @see FinishedGame
 */
typealias ForfeitGameResult = Either<ForfeitGameError, FinishedGame>
