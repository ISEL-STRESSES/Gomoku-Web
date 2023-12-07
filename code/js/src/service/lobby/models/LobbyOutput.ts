import { SirenEntity } from '../../media/siren/SirenEntity';

export interface LobbyOutputModel {
  isGame: boolean,
  id: number,
}

export interface PostRuleIdInputModel {
  ruleId: number;
}

export type LobbyOutput = SirenEntity<LobbyOutputModel>