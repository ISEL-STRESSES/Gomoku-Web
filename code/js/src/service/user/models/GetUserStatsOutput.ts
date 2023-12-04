import { SirenEntity } from '../../media/siren/SirenEntity';

interface GetUserStatsOutputModel {
  userID: number
  username: string
  userRuleStats: RuleStatsModel[]
}

interface RuleStatsModel {
  ruleID: number
  gamesPlayed: number
  elo: number
}

export type GetUserStatsOutput = SirenEntity<GetUserStatsOutputModel>