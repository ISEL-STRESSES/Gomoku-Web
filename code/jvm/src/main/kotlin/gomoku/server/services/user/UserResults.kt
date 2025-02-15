package gomoku.server.services.user

import gomoku.server.domain.user.RankingUserData
import gomoku.server.http.controllers.user.models.userCreate.UserCreateOutputModel
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingError
import gomoku.utils.Either

/**
 * Result for user creation
 * @see UserCreationError
 */
typealias UserCreationResult = Either<UserCreationError, UserCreateOutputModel>

/**
 * Result for token creation
 * @see TokenCreationError
 * @see TokenExternalInfo
 */
typealias TokenCreationResult = Either<TokenCreationError, UserCreateOutputModel>

/**
 * Result for user ranking
 * @see UserCreationError
 * @see RankingUserData
 */
typealias UserRankingResult = Either<UserRankingError, RankingUserData>
