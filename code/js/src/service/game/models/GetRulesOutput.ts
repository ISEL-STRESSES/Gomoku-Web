import { RuleOutputModel } from './RuleOutput';
import { SirenEntity } from '../../media/siren/SirenEntity';

export interface GetRulesOutputModel {
  size: number
}

export type GetRulesOutput = SirenEntity<GetRulesOutputModel, RuleOutputModel>
