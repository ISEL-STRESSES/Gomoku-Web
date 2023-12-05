// Assuming Either.ts contains the Either class and its related functions
import { Either, failure, Failure, success } from '../../utils/Either';
import { SirenEntity, sirenMediaType } from '../media/siren/SirenEntity';
import { Problem, problemMediaType } from '../media/Problem';
import { API_ENDPOINT } from '../home/HomeService';

/**
 * An error that occurs if there is a network error.
 */
export class NetworkError extends Error {
  constructor(message: any) {
    super(message);
  }
}

/**
 * An error that occurs if the response is not a Siren entity.
 */
export class UnexpectedResponseError extends Error {
  constructor(message: string) {
    super(message);
  }
}

async function fetchWithEither(url: string, options: RequestInit): Promise<Either<NetworkError, Response>> {
  try {
    const response = await fetch(url, options);
    return success(response);
  } catch (error) {
    if (error instanceof Error) {
      return failure(new NetworkError(error.message));
    } else {
      return failure(new NetworkError('Unknown network error'));
    }
  }
}

export async function fetchFunction<T>(partialUrl: string, method: string, data: any, headers?: any): Promise<Either<Error | Problem, SirenEntity<any>>> {
  const url = API_ENDPOINT + partialUrl;
  const fetchResult = await fetchWithEither(url, {
    method: method,
    headers: {
      ...headers,
      'Accept': `${sirenMediaType}, ${problemMediaType}`,
    },
    body: JSON.stringify(data),
  });

  if (fetchResult instanceof Failure) {
    return fetchResult;
  } else {
    const response = fetchResult.value;
    const contentType = response.headers.get('Content-Type');
    const body = await response.json();
    if (!response.ok) {
      if (contentType?.includes(problemMediaType)) {
        return failure(new Problem(body));
      } else {
        return failure(new UnexpectedResponseError(`Unexpected response type: ${contentType}`));
      }
    } else {
      if (contentType?.includes(sirenMediaType)) {
        return success(new SirenEntity<T>(body));
      } else {
        return failure(new UnexpectedResponseError(`Unexpected response type: ${contentType}`));
      }
    }
  }
}

// export function useFetch<T>(partialURL: string, method: string, requireAuth: boolean, data: any = undefined): Either<Error | Problem, SirenEntity<T>> | undefined {
//   const [result, setResult] = useState<Either<Error, SirenEntity<T>> | undefined>(undefined);
//   const url = API_ENDPOINT + partialURL;
//   useEffect(() => {
//     let cancelled = false;
//
//     fetchFunction<T>(url, method, data, {
//       ...requireAuth ? { 'Authorization': 'Bearer ' + getCookie(tokenCookie) } : undefined,
//     })
//       .then(eitherResult => {
//         if (!cancelled) {
//           setResult(eitherResult);
//         }
//       })
//       .catch(err => {
//         if (!cancelled) {
//           setResult(failure(err));
//         }
//       });
//
//     return () => {
//       cancelled = true;
//     };
//   }, [url, method, data]);
//
//   return result;
// }
