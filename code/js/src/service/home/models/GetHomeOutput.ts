import { SirenEntity } from '../../media/siren/SirenEntity';


interface GetHomeOutputModel {
  version: string
  authors: AuthorModel[]
}

interface AuthorModel {
  studentID: number
  name: string
  email: string
  socials: SocialModel[]
}

interface SocialModel {
  name: string
  url: string
}

export type GetHomeOutput = SirenEntity<GetHomeOutputModel>