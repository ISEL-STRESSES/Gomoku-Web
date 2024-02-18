import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetUserStatsOutputModel {
  userId: number
  username: string
  size: number
}

export interface RuleStatsModel {
  ruleId: number
  rank: number
  gamesPlayed: number
  elo: number
}

export type GetUserOutput = SirenEntity<GetUserStatsOutputModel, RuleStatsModel>