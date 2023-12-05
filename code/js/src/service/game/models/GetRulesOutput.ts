import { RuleOutputModel } from './RuleOutput';
import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetRulesOutputModel {
  rulesList: RuleOutputModel[];
}

export type GetRulesOutput = SirenEntity<GetRulesOutputModel>;
