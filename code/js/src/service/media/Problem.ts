/**
 * A problem that occurred during the processing of a request.
 *
 * @property type a URI that identifies the problem type
 * @property title A short, human-readable summary of the problem type.
 * @property status the HTTP status code
 * @property detail a human-readable explanation specific to this occurrence of the problem
 * @property instance a URI reference that identifies the specific occurrence of the problem
 *
 * @see <a href="https://tools.ietf.org/html/rfc7807">Problem Details for HTTP APIs</a>
 */
export interface Problem {
    type: string
    title: string
    status?: number
    detail?: string
    instance?: string
}

/**
 * A problem that occurred during the processing of a request.
 * Extends the Error class to allow for the use of the throw keyword.
 *
 * @property type a URI that identifies the problem type
 * @property title A short, human-readable summary of the problem type.
 * @property status the HTTP status code
 * @property detail a human-readable explanation specific to this occurrence of the problem
 * @property instance a URI reference that identifies the specific occurrence of the problem
 *
 * @see <a href="https://tools.ietf.org/html/rfc7807">Problem Details for HTTP APIs</a>
 */
export class Problem extends Error {
    constructor(problem: Problem) {
        super(problem.title)
        this.type = problem.type
        this.title = problem.title
        this.status = problem.status
        this.detail = problem.detail
        this.instance = problem.instance
    }
}

export const problemMediaType = "application/problem+json"
