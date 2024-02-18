import { SirenEntity } from '../../media/siren/SirenEntity';

interface AuthenticationOutputModel {
  userID: number
  username: string
  token: string
  tokenExpiration: number
}

export type AuthenticationOutput = SirenEntity<AuthenticationOutputModel>