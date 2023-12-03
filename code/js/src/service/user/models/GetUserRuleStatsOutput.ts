import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetUserRuleStatsOutputModel {
  id: number,
  username: string,
  ruleID: number,
  gamesPlayed: number,
  elo: number
}

export type GetUserRuleStatsOutput = SirenEntity<GetUserRuleStatsOutputModel>