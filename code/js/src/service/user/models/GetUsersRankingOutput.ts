import { GetUserRuleStatsOutputModel } from './GetUserRuleStatsOutput';
import { SirenEntity } from '../../media/siren/SirenEntity';

interface GetUsersRankingOutputModel {
  ruleID: number,
  size: number
}

export type GetUsersRankingOutput = SirenEntity<GetUsersRankingOutputModel, GetUserRuleStatsOutputModel>