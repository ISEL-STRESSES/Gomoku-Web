import { SirenEntity } from "../../media/siren/SirenEntity";

interface GetTurnOutputModel{
  turn: number;
}

export type GetTurnOutput = SirenEntity<GetTurnOutputModel>