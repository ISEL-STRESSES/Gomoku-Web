package gomoku.server.services.user

import gomoku.server.domain.user.RankingUserData
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingServiceError
import gomoku.utils.Either

/**
 * Result for user creation
 * @see UserCreationError
 */
typealias UserCreationResult = Either<UserCreationError, Int>

/**
 * Result for token creation
 * @see TokenCreationError
 * @see TokenExternalInfo
 */
typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>

/**
 * Result for user ranking
 * @see UserCreationError
 * @see RankingUserData
 */
typealias UserRankingResult = Either<UserRankingServiceError, RankingUserData>
