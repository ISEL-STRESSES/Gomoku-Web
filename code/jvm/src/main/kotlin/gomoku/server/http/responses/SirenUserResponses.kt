package gomoku.server.http.responses

import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.controllers.user.models.UserByIdOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersRankingDataOutputModel
import gomoku.server.http.infra.siren
import gomoku.server.services.user.TokenExternalInfo
import gomoku.server.services.user.UserCreateOutputModel

object SignUp {
    fun siren(body: UserCreateOutputModel) =
        siren(body) {
            clazz(Rel.SIGNUP.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object Login {
    fun siren(body: TokenExternalInfo) =
        siren(body) {
            clazz(Rel.LOGIN.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetRanking {
    fun siren(body: GetUsersRankingDataOutputModel, totalPages: Int, currentOffset: Int, currentLimit: Int) =
        siren(body) {
            clazz(Rel.SEARCH_RANKING.value)
            link(
                "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=$currentOffset",
                Rel.SELF
            )
            link(URIs.HOME, Rel.HOME)

            if (currentOffset + currentLimit < totalPages * currentLimit) { //If we are not on the last page
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=${currentOffset + currentLimit}",
                    Rel.NEXT
                )
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=${(totalPages - 1) * currentLimit}",
                    Rel.LAST
                )
            }

            if (currentOffset > 0) { //If we are not on the first page
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=${if (currentOffset - currentLimit < 0) 0 else currentOffset - currentLimit}",
                    Rel.PREV
                )
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=0",
                    Rel.FIRST
                )
            }
        }

}

object GetUserRanking {
    fun siren(body: UserRuleStatsOutputModel) =
        siren(body) {
            clazz(Rel.USER_RANKING.value)
            link("${URIs.Users.ROOT}/ranking/${body.id}/${body.ruleId}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetUserStats {
    fun siren(body: UserStatsOutputModel) =
        siren(body) {
            clazz(Rel.USER_STATS.value)
            link("${URIs.Users.ROOT}/stats/${body.userId}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object GetUserById {
    fun siren(body: UserByIdOutputModel) =
        siren(body) {
            clazz(Rel.USER.value)
            link(URIs.Users.ROOT + "/${body.uuid}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

object Logout {
    fun siren() =
        siren("User logged out.") {
            clazz(Rel.LOGOUT.value)
            link(URIs.HOME, Rel.HOME)
        }
}

object UserMe {
    fun siren(body: UserHomeOutputModel) =
        siren(body) {
            clazz(Rel.USER.value)
            link(URIs.Users.ROOT + URIs.Users.HOME, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}