import { Either } from "../../utils/Either";
import { Problem } from "../media/Problem";
import { fetchFunction } from "../utils/fetchFunction";
import {
  GetLobbiesModel,
  LobbyModel,
  LobbyOutput,
  PostLobbyIdInputModel,
  PostRuleIdInputModel
} from "./models/LobbyOutput";

export namespace LobbyService {

  /**
   * Creates a new lobby
   *
   * @param rule the data representing the rule
   * @return the API result of the create lobby request
   */
  export async function createLobby(rule: PostRuleIdInputModel): Promise<Either<Error | Problem, LobbyOutput>> {
    const url = `/lobby/create`;
    return fetchFunction(url, "POST", JSON.stringify(rule), true);
  }

  /**
   * Gets a lobby by its ID.
   *
   * @param lobbyId the ID of the lobby
   * @return the API result of the get lobby by ID request
   */
  export async function getLobbyById(lobbyId: number): Promise<Either<Error | Problem, LobbyModel>> {
    const url = `/lobby/${lobbyId}`;
    return fetchFunction(url, "GET", null, true);
  }

  /**
   * Gets all the lobbies.
   *
   * @return the API result of the get lobbies request
   */
  export async function getLobbies(): Promise<Either<Error | Problem, GetLobbiesModel>> {
    const url = `/lobby/`;
    return fetchFunction(url, "GET", null, true);
  }

  /**
   * Joins a lobby by its ID.
   *
   * @param lobby the data representing the lobby
   * @return the API result of the join lobby by ID request
   */
  export async function joinLobby(lobby: PostLobbyIdInputModel): Promise<Either<Error | Problem, LobbyOutput>> {
    const url = `/lobby/join`;
    return fetchFunction(url, "POST", JSON.stringify(lobby), true);
  }

  /**
   * Join by match-make.
   *
   * @param rule the data representing the rule
   * @return the API result of the create lobby request
   */
  export async function joinByMatchMake(rule: PostRuleIdInputModel): Promise<Either<Error | Problem, LobbyOutput>> {
    const url = `/lobby/start`;
    return fetchFunction(url, "POST", JSON.stringify(rule), true);
  }

  /**
   * Leave a lobby by its ID.
   *
   * @param lobby the data representing the lobby
   * @return the API result of the leave lobby by ID request
   */
  export async function leaveLobby(lobby: PostLobbyIdInputModel): Promise<Either<Error | Problem, LobbyOutput>> {
    const url = `/lobby/leave`;
    return fetchFunction(url, "POST", JSON.stringify(lobby), true);
  }
}