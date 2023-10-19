package gomoku.server.services.user

import gomoku.server.services.errors.user.LoginError
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.utils.Either

/**
 * Result for user creation
 * @see UserCreationError
 */
typealias UserCreationResult = Either<UserCreationError, Int>

/**
 * Result for login
 * @see LoginError
 * @see TokenExternalInfo
 */
typealias LoginResult = Either<LoginError, TokenExternalInfo>

/**
 * Result for token creation
 * @see TokenCreationError
 * @see TokenExternalInfo
 */
typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>
