package gomoku.server.services.user

import gomoku.server.services.errors.LoginError
import gomoku.server.services.errors.TokenCreationError
import gomoku.server.services.errors.UserCreationError
import gomoku.utils.Either

typealias UserCreationResult = Either<UserCreationError, Int>

typealias LoginResult = Either<LoginError, TokenExternalInfo>

typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>
