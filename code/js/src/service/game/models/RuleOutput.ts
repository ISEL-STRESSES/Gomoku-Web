import { SirenEntity } from '../../media/siren/SirenEntity';

export interface RuleOutputModel {
  ruleId: number;
  boardSize: number;
  variant: string;
  openingRule: string;
}

export interface RuleModel {
  ruleId: number
  boardSize: string
  type: string
}

export type RuleOutput = SirenEntity<RuleOutputModel>;
