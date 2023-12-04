import { SirenEntity } from '../../media/siren/SirenEntity';

interface GetUserByIdOutputModel {
  userID: number
  username: string
}

export type GetUserByIdOutput = SirenEntity<GetUserByIdOutputModel>