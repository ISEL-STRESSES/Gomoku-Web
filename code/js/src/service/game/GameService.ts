import { fetchFunction } from '../utils/fetchFunction';
import { Either } from '../../utils/Either';
import { Problem } from '../media/Problem';
import { GetRulesOutput } from './models/GetRulesOutput';
import { RuleOutput } from './models/RuleOutput';
import { GameOutput } from './models/GameOutput';
import { PlayPositionInputModel } from "./models/PlayPositionInputModel";
import { GetTurnOutput } from "./models/GetTurnOutput";

export namespace GameService {

  /**
   * Gets the hub (list of games, I assume).
   *
   * @return the API result of the get hub request
   */
  export async function getHub(): Promise<Either<Error | Problem, any>> { // Replace any with the actual expected output type
    const url = `/game/`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets a game by its ID.
   *
   * @param gameId the ID of the game
   * @return the API result of the get game by ID request
   */
  export async function getGameById(gameId: number): Promise<Either<Error | Problem, GameOutput>> {
    const url = `/game/${gameId}`;
    return fetchFunction(url, "GET", null, true);
  }

  /**
   * Makes a play in a game.
   *
   * @param gameId the ID of the game
   * @param playData the data representing the play
   * @return the API result of the make play request
   */
  export async function makePlay(gameId: number, playData: PlayPositionInputModel): Promise<Either<Error | Problem, GameOutput>> {
    const url = `/game/${gameId}/play`;
    return fetchFunction(url, "POST", JSON.stringify(playData), true);
  }

  /**
   * Gets the game rules.
   *
   * @return the API result of the get game rules request
   */
  export async function getGameRules(): Promise<Either<Error | Problem, GetRulesOutput>> {
    const url = `/game/rules`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets a game rule by ID.
   *
   * @param ruleId the ID of the rule
   * @return the API result of the get game rule by ID request
   */
  export async function getGameRuleById(ruleId: number): Promise<Either<Error | Problem, RuleOutput>> {
    const url = `/game/rules/${ruleId}`;
    return fetchFunction(url, "GET", null);
  }

  /**
   * Gets the current turn information for a game.
   *
   * @param gameId the ID of the game
   * @return the API result of the get turn request
   */
  export async function getTurn(gameId: number): Promise<Either<Error | Problem, GetTurnOutput>> {
    const url = `/game/${gameId}/turn`;
    return fetchFunction(url, "GET", null, true);
  }

  /**
   * Forfeits a game by its ID.
   *
   * @param gameId the ID of the game
   * @return the API result of the forfeit game request
   */
  export async function forfeitGame(gameId: number): Promise<Either<Error | Problem, GameOutput>> {
    const url = `/game/${gameId}/forfeit`;
    return fetchFunction(url, "POST", null);
  }
}
