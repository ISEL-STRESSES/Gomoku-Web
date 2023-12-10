import { SirenEntity } from '../../media/siren/SirenEntity';
import { RuleModel } from "../../game/models/RuleOutput";

export interface LobbyOutputModel {
  isGame: boolean,
  id: number,
}

export interface GetLobbyModel {
  id: number,
  rule: RuleModel,
  userId: number,
  state: boolean,
  gameId: number
}

export interface PostRuleIdInputModel {
  ruleId: number;
}

export interface PostLobbyIdInputModel {
  lobbyId: number;
}

export interface SizeOutputModel {
  size: number;
}

export type LobbyOutput = SirenEntity<LobbyOutputModel>

export type LobbyModel = SirenEntity<GetLobbyModel>

export type GetLobbiesModel = SirenEntity<SizeOutputModel, GetLobbyModel>