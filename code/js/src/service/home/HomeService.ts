import { GetHomeOutput } from './models/GetHomeOutput';
import { fetchFunction } from '../utils/fetchFunction';
import { Either } from '../../utils/Either';
import { Problem } from '../media/Problem';

export const API_ENDPOINT = "http://localhost:8000/api"

export namespace HomeService {

      /**
      * Gets the home information.
      *
      * @return the API result of the get home request
      */
      export function getHome(): Promise<Either<Error | Problem, GetHomeOutput>> {
            return fetchFunction("/", "GET", false)
      }
}