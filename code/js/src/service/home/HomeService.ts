import { GetHomeOutput } from './models/GetHomeOutput';
import { useFetch } from '../utils/useFetch';
import { Either } from '../../utils/Either';

export const API_ENDPOINT = "http://localhost:8000/api"

export namespace HomeService {

      /**
      * Gets the home information.
      *
      * @return the API result of the get home request
      */
      export function getHome(): Either<Error, GetHomeOutput> | undefined {
            return useFetch("/", "GET", false)
      }
}