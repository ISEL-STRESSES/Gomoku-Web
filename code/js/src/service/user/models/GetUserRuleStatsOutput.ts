import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetUserRuleStatsOutputModel {
  id: number,
  rank: number,
  username: string,
  gamesPlayed: number,
  elo: number
}

export type GetUserRuleStatsOutput = SirenEntity<GetUserRuleStatsOutputModel>