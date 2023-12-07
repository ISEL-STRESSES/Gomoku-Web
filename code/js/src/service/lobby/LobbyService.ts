import { Either } from "../../utils/Either";
import { Problem } from "../media/Problem";
import { fetchFunction } from "../utils/fetchFunction";
import { LobbyOutput, PostRuleIdInputModel } from "./models/LobbyOutput";

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
}