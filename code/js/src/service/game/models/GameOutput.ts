import { SirenEntity } from '../../media/siren/SirenEntity';
import { RuleOutputModel } from './RuleOutput';

/**
 * The Game Output.
 *
 * @property id the id of the game
 * @property playerBlack the id of the black player
 * @property playerWhite the id of the white player
 * @property rule the rule of the game
 * @property moves the moves of the game
 * @property gameOutcome the outcome of the game, present if type is "FINISHES"
 * @property turn the turn of the game, present if type is "ONGOING"
 * @property type the type of the game, either "ONGOING" or "FINISHED"
 */
export interface GameOutputModel {
  id: number;
  playerBlack: number;
  playerWhite: number;
  rule: RuleOutputModel;
  moves: MovesOutputModel;
  gameOutcome: string | null;
  turn: string | null;
  type: string;
}

/**
 * The Moves Output.
 *
 * @property boardSize the size of the board
 * @property orderOfMoves the order of the moves
 */
export interface MovesOutputModel {
  boardSize: number;
  orderOfMoves: MoveOutputModel[];
}

/**
 * The Move Output.
 *
 * @property position the position of the move
 * @property color the color of the move, either "BLACK" or "WHITE"
 */
export interface MoveOutputModel {
  position: PositionOutputModel;
  color: string;
}

/**
 * The Position Output.
 *
 * @property x the x coordinate of the position
 * @property y the y coordinate of the position
 */
export interface PositionOutputModel {
  x: number;
  y: number;
}

export type GameOutput = SirenEntity<GameOutputModel>;
