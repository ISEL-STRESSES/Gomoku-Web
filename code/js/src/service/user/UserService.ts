import { fetchFunction } from '../utils/fetchFunction';
import { Either } from '../../utils/Either';
import { Problem } from '../media/Problem';
import { AuthenticationOutput } from './models/AuthenticationOutput';
import { GetUserByIdOutput } from './models/GetUserByIdOutput';
import { GetUsersRankingOutput } from './models/GetUsersRankingOutput';
import { GetUserRuleStatsOutput } from './models/GetUserRuleStatsOutput';
import { GetUserStatsOutput } from './models/GetUserStatsOutput';
import { LogoutOutput } from './models/LogoutOutput';

export namespace UserService {

  /**
   * Gets user stats.
   *
   * @param userID the id of the user
   *
   * @return the API result of the get user stats request
   */
  export async function getUserStats(userID: number): Promise<Either<Error | Problem, GetUserStatsOutput>> {
    const url = `/users/stats/${userID}`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets user stats for a given rule
   *
   * @param userID the id of the user
   * @param ruleID the id of the rule
   *
   * @return the API result of the get user stats request
   */
  export async function getUserStatsForRule(userID: number, ruleID: number): Promise<Either<Error | Problem, GetUserRuleStatsOutput>> {
    const url = `/users/${userID}/ranking/${ruleID}`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets the ranking of the users for a given rule
   *
   * @param ruleID the id of the rule
   * @param username the username to search
   *
   * @return the API result of the get-ranking request
   */
  export async function getRanking(ruleID: number, username?: string): Promise<Either<Error | Problem, GetUsersRankingOutput>> {
    let url = `/users/ranking/${ruleID}`;
    if (username) {
      url += `?username=${username}`;
    }
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets a user by its id
   *
   * @param userID the id of the user
   *
   * @return the API result of the get user request
   */
  export async function getUser(userID: number): Promise<Either<Error | Problem, GetUserByIdOutput>> {
    const url = `/users/${userID}`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Signs up a user
   *
   * @param username the username of the user
   * @param password the password of the user
   *
   * @return the API result of the sign-up request
   */
  export async function signUp(username: string, password: string): Promise<Either<Error | Problem, AuthenticationOutput>> {
    const url = `/users/create`;
    const sendTokenViaCookie = "true";
    const data = JSON.stringify({ username, password, sendTokenViaCookie });
    return fetchFunction(url, "POST", data);
  }

  /**
  * Logs in a user
  *
  * @param username the username of the user
  * @param password the password of the user
  *
  * @return the API result of the login request
  */
  export async function login(username: string, password: string): Promise<Either<Error | Problem, AuthenticationOutput>> {
    const url = `/users/token`;
    const sendTokenViaCookie = "true";
    const data = JSON.stringify({ username, password, sendTokenViaCookie });
    console.log(data);
    return fetchFunction(url, "POST", data);
  }

  /**
   * Logs out a user
   *
   * @return the API result of the logout request
   */
  export async function logout(): Promise<Either<Error | Problem, LogoutOutput>> {
    const url = `/users/logout`;
    return fetchFunction(url, "POST", null);
  }
}
