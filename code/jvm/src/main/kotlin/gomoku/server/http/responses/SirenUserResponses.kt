package gomoku.server.http.responses

import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.controllers.user.models.UserByIdOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersRankingDataOutputModel
import gomoku.server.http.infra.siren
import gomoku.server.services.user.UserCreateOutputModel

object SignUp {
    fun siren(body: UserCreateOutputModel) =
        siren(body) {
            clazz("user")
            link(URIs.Users.ROOT + URIs.Users.CREATE, Rel.SELF)
        }
}

object Login {
    fun siren(body: UserCreateOutputModel) =
        siren(body) {
            clazz("user")
            link(URIs.Users.ROOT + URIs.Users.TOKEN, Rel.SELF)
        }
}

object GetRanking {
    fun siren(body: GetUsersRankingDataOutputModel, totalPages: Int, currentOffset: Int, currentLimit: Int) =
        siren(body) {
            clazz("user-ranking-search")
            link(
                "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=${body.limit}&offset=${body.offset}",
                Rel.SELF
            )

            if (currentOffset + currentLimit < totalPages * currentLimit) { //If we are not on the last page
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=${body.limit}&offset=${currentOffset + currentLimit}",
                    Rel.NEXT
                )
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=${body.limit}&offset=${(totalPages - 1) * currentLimit}",
                    Rel.LAST
                )
            }

            if (currentOffset > 0) { //If we are not on the first page
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=${body.limit}&offset=${if (currentOffset - currentLimit < 0) 0 else currentOffset - currentLimit}",
                    Rel.PREV
                )
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=${body.limit}&offset=0",
                    Rel.FIRST
                )
            }
        }

}

object GetUserRanking {
    fun siren(body: UserRuleStatsOutputModel) =
        siren(body) {
            clazz("user-ranking-search")
            link("${URIs.Users.ROOT}/ranking/${body.id}/${body.ruleId}", Rel.SELF)
        }
}

object GetUserStats {
    fun siren(body: UserStatsOutputModel) =
        siren(body) {
            clazz("user-stats-search")
            link("${URIs.Users.ROOT}/stats/${body.userId}", Rel.SELF)
        }
}

object GetUserById {
    fun siren(body: UserByIdOutputModel) =
        siren(body) {
            clazz("user")
            link(URIs.Users.ROOT + "/${body.uuid}", Rel.SELF)
        }
}

object Logout {
    fun siren() =
        siren("User logged out.") {
            clazz("logout")
            link(URIs.Users.ROOT + URIs.Users.LOGOUT, Rel.SELF)
        }
}

object UserMe {
    fun siren(body: UserHomeOutputModel) =
        siren(body) {
            clazz("user")
            link(URIs.Users.ROOT + URIs.Users.HOME, Rel.SELF)
        }
}

// TODO: Add the remaining responses if needed
