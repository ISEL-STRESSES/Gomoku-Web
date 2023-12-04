import { GetUserRuleStatsOutputModel } from './GetUserRuleStatsOutput';
import { SirenEntity } from '../../media/siren/SirenEntity';

interface GetUsersRankingOutputModel {
  userData: GetUserRuleStatsOutputModel[],
  ruleID: number,
  search: string,
  //TODO: Pagination, we shouldn't compromise in giving limit and offset in the output instead use the links to get the next page.
  total: number
}

export type GetUsersRankingOutput = SirenEntity<GetUsersRankingOutputModel>