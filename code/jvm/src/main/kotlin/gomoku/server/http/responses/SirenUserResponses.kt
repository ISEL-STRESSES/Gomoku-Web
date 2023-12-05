package gomoku.server.http.responses

import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.controllers.user.models.UserByIdOutputModel
import gomoku.server.http.controllers.user.models.UserCreateOutputModel
import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel
import gomoku.server.http.controllers.user.models.UserStatsOutputModel
import gomoku.server.http.controllers.user.models.getHome.UserHomeOutputModel
import gomoku.server.http.controllers.user.models.getUsersData.GetUsersRankingDataOutputModel
import gomoku.server.http.infra.EntityModel
import gomoku.server.http.infra.LinkModel
import gomoku.server.http.infra.PropertyRankingModel
import gomoku.server.http.infra.PropertyUserStatsModel
import gomoku.server.http.infra.siren

/**
 * TODO
 */
object SignUp {

    /**
     * TODO
     */
    fun siren(body: UserCreateOutputModel) =
        siren {
            clazz(Rel.SIGNUP.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object Login {

    /**
     * TODO
     */
    fun siren(body: UserCreateOutputModel) =
        siren {
            clazz(Rel.LOGIN.value)
            property(body)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetRanking {

    /**
     * TODO
     */
    fun siren(body: GetUsersRankingDataOutputModel, totalPages: Int, currentOffset: Int, currentLimit: Int) =
        siren {
            clazz(Rel.SEARCH_RANKING.value)
            property(PropertyRankingModel(body.ruleId, body.userData.size))
            body.userData.forEach {
                entity(EntityModel(listOf(Rel.USER.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Users.ROOT + "/${it.id}/ranking/${body.ruleId}"))))
            }
            link(
                "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=$currentOffset",
                Rel.SELF
            )
            link(URIs.HOME, Rel.HOME)

            if (currentOffset + currentLimit < totalPages * currentLimit) { // If we are not on the last page
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=${currentOffset + currentLimit}",
                    Rel.NEXT
                )
                link(
                    "${URIs.Users.ROOT}/ranking/${body.ruleId}?username=${body.search}&limit=$currentLimit&offset=${(totalPages - 1) * currentLimit}",
                    Rel.LAST
                )
            }

            if (currentOffset > 0) { // If we are not on the first page
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

/**
 * TODO
 */
object GetUserRanking {

    /**
     * TODO
     */
    fun siren(body: UserRuleStatsOutputModel, ruleId: Int) =
        siren {
            clazz(Rel.USER_RANKING.value)
            property(body)
            link("${URIs.Users.ROOT}/${body.id}/ranking/$ruleId", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetUserStats {

    /**
     * TODO
     */
    fun siren(body: UserStatsOutputModel) =
        siren {
            clazz(Rel.USER_STATS.value)
            property(PropertyUserStatsModel(body.userId, body.username, body.userRuleStats.size))
            body.userRuleStats.forEach {
                entity(EntityModel(listOf(Rel.USER_RANKING.value), emptyList(), it, listOf(LinkModel(listOf(Rel.SELF.value), URIs.Users.ROOT + "/${body.userId}/ranking/${it.ruleId}"))))
            }
            link("${URIs.Users.ROOT}/stats/${body.userId}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object GetUserById {

    /**
     * TODO
     */
    fun siren(body: UserByIdOutputModel) =
        siren {
            clazz(Rel.USER.value)
            property(body)
            link(URIs.Users.ROOT + "/${body.uuid}", Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object Logout {

    /**
     * TODO
     */
    fun siren() =
        siren {
            clazz(Rel.LOGOUT.value)
            property("User logged out.")
            link(URIs.HOME, Rel.HOME)
        }
}

/**
 * TODO
 */
object UserMe {

    /**
     * TODO
     */
    fun siren(body: UserHomeOutputModel) =
        siren {
            clazz(Rel.USER.value)
            property(body)
            link(URIs.Users.ROOT + URIs.Users.HOME, Rel.SELF)
            link(URIs.HOME, Rel.HOME)
        }
}
