// Assuming Either.ts contains the Either class and its related functions
import { useEffect, useState } from 'react';
import { Either, failure, Failure, success } from '../../utils/Either';
import { getCookie } from '../../utils/cookieUtils';
import { tokenCookie } from '../../components/authentication/Authn';
import { SirenEntity, sirenMediaType } from '../media/siren/SirenEntity';
import { Problem, problemMediaType } from '../media/Problem';

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

async function fetchFunction<T>(url: string, method: string, data: any, headers: any): Promise<Either<Problem, SirenEntity<any>>> {
  const fetchResult = await fetchWithEither(url, {
    method: method,
    headers: {
      ...headers,
      'Accept': `${sirenMediaType}, ${problemMediaType}`,
    },
    body: JSON.stringify(data),
  });

  if (fetchResult instanceof Failure) {
    throw fetchResult.value;// TODO: How can i handle if it's a NetworkError or UnexpectedResponseError? just throw it and have a handler for it in the component?
  } else {
    const response = fetchResult.value;
    const contentType = response.headers.get('Content-Type');
    const body = await response.json();
    if (!response.ok) {
      if (contentType?.includes(problemMediaType)) {
        return failure(new Problem(body));
      } else {
        throw new UnexpectedResponseError(`Unexpected response type: ${contentType}`);
      }
    } else {
      if (contentType?.includes(sirenMediaType)) {
        return success(new SirenEntity<T>(body));
      } else {
        throw new UnexpectedResponseError(`Unexpected response type: ${contentType}`);
      }
    }
  }
}

export function useFetch<T>(url: string, method: string, data: any): {
  result: Either<Problem | Error, SirenEntity<T>> | undefined, //TODO: This is very smelly, no better way to do this?
  isLoading: boolean,
} {
  const [result, setResult] = useState<Either<any, any>>();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    let cancelled = false;
    setIsLoading(true);

    fetchFunction(url, method, data, { //TODO: Explore if this is really needed
      'Authorization': 'Bearer ' + getCookie(tokenCookie), //TODO: Even in the case of a public endpoint, the token is sent.
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'PUT',
      'Access-Control-Allow-Headers': 'content-type, x-requested-with, Authorization',
    })
      .then(eitherResult => {
        if (!cancelled) {
          setResult(eitherResult);
          setIsLoading(false);
        }
      })
      .catch(err => {
        if (!cancelled) {
          setIsLoading(false);
          setResult(failure(err));
        }
      });

    return () => {
      cancelled = true;
    };
  }, [url, method, data]);

  return { result, isLoading };
}

