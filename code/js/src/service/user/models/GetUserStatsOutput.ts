import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetUserStatsOutputModel {
  userID: number
  username: string
  size: number
}

export interface RuleStatsModel {
  ruleID: number
  rank: number
  gamesPlayed: number
  elo: number
}

export type GetUserOutput = SirenEntity<GetUserStatsOutputModel, RuleStatsModel>