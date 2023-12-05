import { SirenEntity } from '../../media/siren/SirenEntity';

export interface RuleOutputModel {
  ruleId: number;
  boardSize: number;
  variant: string;
  openingRule: string;
}

export type RuleOutput = SirenEntity<RuleOutputModel>;
