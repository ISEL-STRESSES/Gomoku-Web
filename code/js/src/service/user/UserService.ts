import { useFetch } from '../utils/useFetch';
import { Either } from '../../utils/Either';
import { SirenEntity } from '../media/siren/SirenEntity';
import { GetUserStatsOutput } from './models/GetUserStatsOutput';
import { GetUserRuleStatsOutput } from './models/GetUserRuleStatsOutput';
import { GetUsersRankingOutput } from './models/GetUsersRankingOutput';
import { GetUserByIdOutput } from './models/GetUserByIdOutput';
import { AuthenticationOutput } from './models/AuthenticationOutput';


export namespace UserService {

  //TODO: Either centralize URIs or find them from the link map using hypermedia

    /**
     * Gets user stats.
     *
     * @param userID the id of the user
     *
     * @return the API result of the get user stats request
     */
    export function getUserStats(userID: number): Either<Error, GetUserStatsOutput> | undefined {
        return useFetch(`/users/stats/${userID}`, "GET", false)
    }

    /**
     * Gets user stats for a given rule
     *
     * @param userID the id of the user
     * @param ruleID the id of the rule
     *
     * @return the API result of the get user stats request
     */
    export function getUserStatsForRule(userID: number, ruleID: number): Either<Error, GetUserRuleStatsOutput> | undefined {
      return useFetch(`/users/${userID}/ranking/${ruleID}`, "GET", false)
    }

  /**
   * Gets the ranking of the users for a given rule
   *
   * @param ruleID the id of the rule
   * @param username the username to search
   *
   * @return the API result of the get-ranking request
   */
  export function getRanking(ruleID: number, username?: string): Either<Error, GetUsersRankingOutput> | undefined { //TODO: Figure out how to do pagination
    if (username) {
      return useFetch(`/users/ranking/${ruleID}?username=${username}`, "GET", false)
    } else {
      return useFetch(`/users/ranking/${ruleID}`, "GET", false)
    }
  }

  /**
   * Gets a user by its id
   *
   * @param userID the id of the user
   *
   * @return the API result of the get user request
   */
  export function getUser(userID: number): Either<Error, GetUserByIdOutput> | undefined {
    return useFetch(`/users/${userID}`, "GET", false)
  }

  /**
   * Signs up a user
   *
   * @param username the username of the user
   * @param password the password of the user
   *
   * @return the API result of the sign-up request
   */
  export function signUp(username: string, password: string): Either<Error, AuthenticationOutput> | undefined {
    return useFetch(`/users/create`, "POST", false, JSON.stringify({ username, password }))
  }

  /**
   * Logs in a user
   *
   * @param username the username of the user
   * @param password the password of the user
   *
   * @return the API result of the login request
   */
  export function login(username: string, password: string): Either<Error, AuthenticationOutput> | undefined {
    return useFetch(`/users/token`, "POST", false, JSON.stringify({ username, password }))
  }

  /**
   * Logs out a user
   *
   * @return the API result of the logout request
   */
  export function logout(): Either<Error, SirenEntity<string>> | undefined {
    return useFetch(`/users/logout`, "POST", true)
  }
}