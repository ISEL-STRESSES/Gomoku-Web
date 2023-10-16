package gomoku.server.services.user

import gomoku.server.services.errors.user.LoginError
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.utils.Either

typealias UserCreationResult = Either<UserCreationError, Int>

typealias LoginResult = Either<LoginError, TokenExternalInfo>

typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>
