# Gomoku API

> version: 0.1.1

Web-based system that allows multiple players to play the Gomoku game, a strategy board game for two players, who take
turns placing a stone of their color on an empty intersection. This API facilitates game creation, user management,
and gameplay.

## Table of Contents

- [Introduction](#introduction) (Incomplete)
- [Pagination](#pagination) (TODO)
- [Authentication](#authentication) (TODO)
- [Endpoints](#endpoints) (Incomplete)
    - [Home](#home)
        - [Get Home](#get-home)
    - [Users](#users) (Incomplete)
        - [Create a New User](#create-a-new-user) (Incomplete)
        - [Logs a User Out](#logs-a-user-out) (Incomplete)
        - [Gets a user home page](#gets-a-user-home-page) (Incomplete)
        - [Gets the ranking of the users for a given rule](#gets-the-ranking-of-the-users-for-a-given-rule) (Incomplete)
        - [Gets the ranking of a user for a given rule](#gets-the-ranking-of-a-user-for-a-given-rule) (Incomplete)
        - [Gets the whole stats of a user](#gets-the-whole-stats-of-a-user) (Incomplete)
        - [Creates a token for a user](#creates-a-token-for-a-user) (Incomplete)
        - [Gets the user with the given id](#gets-the-user-with-the-given-id) (Incomplete)
    - [Games](#games) (Incomplete)
        - [Get the Finished games](#get-finished-games) (Incomplete)
        - [Get the available rules](#get-game-rules) (Incomplete)
        - [Get the details of a game](#get-game-details) (Incomplete)
        - [Makes a move in a game](#make-a-move) (Incomplete)
        - [Gets the player id of the current turn](#get-current-turn-player-id) (Incomplete)
        - [Leaves a lobby](#leave-a-lobby) (Incomplete)
        - [Starts the matchmaking process](#start-matchmaking-process) (Incomplete)
- [Types](#types) (Incomplete)
    - [Parameters](#parameters)
        - [Password](#password)
        - [Username](#username)
    - [Success Types](#success-types) (Incomplete)
        - [Home](#home-1)
        - [User Created](#user-created) (Incomplete)
        - [User Logged in](#user-logged-in) (Incomplete)
    - [Error Types](#error-types) (Incomplete)
        - [Bad Request](#bad-request) (Incomplete)
        - [User or Password Invalid](#user-or-password-invalid) (Incomplete)
        - [Invalid Username](#invalid-username) (Incomplete)
        - [Insecure Password](#insecure-password) (Incomplete)
        - [Token Not Revoked](#token-not-revoked) (Incomplete)
        - [User Not Found](#user-not-found) (Incomplete)
        - [Rule Not Found](#rule-not-found) (Incomplete)
        - [User Stats Not Found](#user-stats-not-found) (Incomplete)
        - [User Already Exits](#user-already-exists) (Incomplete)
        - [Same Player](#same-player) (Incomplete)
        - [Game Already Finished](#game-already-finished) (Incomplete)
        - [Impossible Position](#impossible-position) (Incomplete)
        - [Not Your Turn](#not-your-turn) (Incomplete)
        - [Invalid Move](#invalid-move) (Incomplete)
        - [Player Not in Game](#player-not-in-game) (Incomplete)
        - [Game Not Found](#game-not-found) (Incomplete)
        - [No Rules Found](#no-rules-found) (Incomplete)
        - [Lobby Not Found](#lobby-not-found) (Incomplete)
        - [Position Already Occupied](#position-already-occupied) (Incomplete)
        - [Internal Server Error](#internal-server-error) (Incomplete)
        - [Make Move Failed](#make-move-failed) (Incomplete)
        - [Leave Lobby Failed](#leave-lobby-failed) (Incomplete)

## Introduction

For the purpose of the URLs in this document, the base URL is `https://localhost:8080/api/`.

## Pagination

The pagination is done with the header `Link` and the following format:

```

```

## Authentication

_TODO_

## Endpoints

### Home

#### Get Home

Retrieves basic information or status of the Gomoku API.

- **URL:** `/api/`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Home](#home-1)
- **Error Response:**
    - **Content:**
        - application/problem+json
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
  ```bash
  curl https://localhost:8080/api/
    ```

### Users

#### Create a new user

- **URL:** `/api/users/create`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User Created](#user-created)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Bad Request](#bad-request)
            - [User Already Exists](#user-already-exists)
            - [Internal Server Error](#internal-server-error)

- **Sample Call:**
    ```bash
    curl -X POST -d "username=foo&password=bar" https://localhost:8080/api/users/create
    ```

#### Logs a User in

- **URL:** `/api/users/token`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
- **Content:**
    - application/vnd.siren+json
        - [User Logged in](#user-logged-in)

#### Logs a User out

- **URL:** `/api/users/logout`
- **Method:** `POST`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - _TODO_
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/users/logout
    ```

#### Gets a User Home Page

Retrieves user-specific home page

- **URL:** `/api/users/me`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User Home](#user-home)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/me
    ```

#### Gets the ranking of the users for a given rule

Gets the ranking of the users for a given rule

- **URL:** `/api/users/ranking{rukeId}`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `ruleId=[integer]`
        - [Username](#username)
    - **Optional:**
        - `offset=[integer]`
        - `limit=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Rule Not Found](#rule-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/ranking?ruleId=1&username=foo
    ```

#### Gets the ranking of a user for a given rule

Gets the ranking of a user for a given rule

- **URL:** `/api/users/ranking{rukeId}/{userId}`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `ruleId=[integer]`
        - `userId=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [User Not Found](#user-not-found)
            - [Rule Not Found](#rule-not-found)
            - [User Stats Not Found](#user-stats-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/ranking?ruleId=1&userId=1
    ```

#### Gets the whole stats of a user

Gets the whole stats of a user

- **URL:** `/api/users/stats/{userId}`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `userId=[integer]`
- **Success Response:**
- **Content:**
    - application/vnd.siren+json
        - [User Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [User Not Found](#user-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/stats/1
    ```

#### Creates a token for a user

Creates a token for a user

- **URL:** `/api/users/token`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User Logged in](#user-logged-in)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [User or Password Invalid](#user-or-password-invalid)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST -d "username=foo&password=bar" https://localhost:8080/api/users/token
    ```

#### Gets the user with the given id

Gets the user with the given id

- **URL:** `/api/users/{userId}`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `userId=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [User](#user)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [User Not Found](#user-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/1
    ```

### Games

#### Get Finished Games

Retrieves a list of finished games

- **URL:** `/api/game/`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Finished Games](#finished-games)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/
    ```

#### Get Game Rules

Retrieves available game rules

- **URL:** `/api/game/rules`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Available Rules](#available-rules)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [No Rules Found](#no-rules-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/rules
    ```

#### Get Game Details

Retrieves the details of a game

- **URL:** `/api/game/{id}`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `id=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Game Details](#game-details)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Game Not Found](#game-not-found)
            - [User Not Found](#user-not-found)
            - [Player Not in Game](#player-not-in-game)
            - [Internal Server Error](#internal-server-error)

#### Make a Move

Makes a move in a game

- **URL:** `/api/game/{id}/play`
- **Method:** `POST`
- **URL Params:**
    - **Required:**
        - `id=[integer]` (id of the game)
- **Query Params:**
    - **Required:**
        - `x=[integer]`
        - `y=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Game Details](#game-details)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Game Already Finished](#game-already-finished)
            - [Impossible Position](#impossible-position)
            - [Not Your Turn](#not-your-turn)
            - [Invalid Move](#invalid-move)
            - [Player Not in Game](#player-not-in-game)
            - [Game Not Found](#game-not-found)
            - [User Not Found](#user-not-found)
            - [Position Already Occupied](#position-already-occupied)
            - [Make Move Failed](#make-move-failed)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/1/play?pos=1
    ```

#### Get Current Turn Player Id

Retrieves the player id of the current turn

- **URL:** `/api/game/{id}/turn`
- **Method:** `GET`
- **URL Params:**
    - **Required:**
        - `id=[integer]` (id of the game)
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Current Turn Player id](#current-turn-player-id)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Game Already Finished](#game-already-finished)
            - [Game Not Found](#game-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/1/turn
    ```

#### Leave a Lobby

Leaves a lobby

- **URL:** `/api/game/{lobbyId}/leave`
- **Method:** `POST`
- **URL Params:**
    - **Required:**
        - `lobbyId=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Lobby Not Found](#lobby-not-found)
            - [User Not in Lobby](#user-not-found)
            - [Leave Lobby Failed](#leave-lobby-failed)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/1/leave
    ```

#### Start Matchmaking Process

Starts the matchmaking process

- **URL:** `/api/game/{rulesId}`
- **Method:** `POST`
- **URL Params:**
    - **Required:**
        - `rulesId=[integer]`
- **Success Response:**
    - **Content:**
        - application/vnd.siren+json
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - application/problem+json
            - [Same Player](#same-player)
            - [Leave Lobby Failed](#leave-lobby-failed)
            - [Internal Server Error](#internal-server-error)

## Types

### Parameters

#### Password

The password is a string with a length between 6 and 30 characters
It has to contain at least one lowercase letter, one uppercase letter, one digit and one special character.

#### Username

The username is a string with a length between 3 and 50 characters, consisting solely of non-whitespace characters.

### Success Types

#### Home

The application home page has the server info and the authors' info with their socials.

- **Sample Success Response:**

```vnd.siren+json
{
  "class": [
    "home"
  ],
  "properties": {
    "version": "0.0.1",
    "authors": [
      {
        "studentID": 0,
        "name": "John Doe",
        "email": "foo@somedomain.something",
        "socials": [
          {
            "name": "something",
            "url": "https://somewebsite.something"
          }
        ]
      }
    ]
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/"
    }
  ],
  "actions": [
    {
      "name": "signup",
      "href": "/create",
      "method": "POST"
    },
    {
      "name": "login",
      "href": "/token",
      "method": "POST"
    }
  ]
}
```

#### User Created

- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "user"
    ],
    "properties": {
        "userId": 29,
        "token": "QWayEE_Z7Qq-iEZvRX6pTQ1bCeDeDOex_ihiX5pLt4k="
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/create"
        }
    ],
    "actions": []
}
```

#### User Logged in

- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "user"
    ],
    "properties": {
        "userId": 25,
        "token": "oZ5qi3zf8Qi31vJqQit0-s5w6Doh698oQNEC0zk56Rg="
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/token"
        }
    ],
    "actions": []
}
```

#### User Stats

- **Sample Response:**

```json
{
  "userId": 530,
  "username": "DAW_Demo1",
  "userRuleStats": []
}
```

#### Available Rules

- **Sample Response:**

```json
{
  "rulesList": [
    {
      "ruleId": 1,
      "boardSize": 15,
      "variant": "STANDARD",
      "openingRule": "FREE"
    },
    {
      "ruleId": 2,
      "boardSize": 19,
      "variant": "STANDARD",
      "openingRule": "FREE"
    },
    {
      "ruleId": 3,
      "boardSize": 15,
      "variant": "STANDARD",
      "openingRule": "PRO"
    }
  ]
}
```

#### Finished Games

- **Sample Response:**

```json
{
  "finishedGames": [
    {
      "id": "integer",
      "playerBlack": "integer",
      "playerWhite": "integer",
      "rule": {
        "ruleId": "integer",
        "boardSize": "integer",
        "variant": "string",
        "openingRule": "string"
      },
      "moves": {
        "boardSize": "integer",
        "orderOfMoves": [
          {
            "pos": "integer",
            "color": "string"
          }
        ]
      },
      "GameOutcome": "string",
      "turn": "string",
      "type": "string"
    }
  ]
}
```

### Error Types

#### Bad Request

This error happens when for example you try to create a user and the username or password is invalid.

- **Sample Error Response:**

```json
{
  "title": "Invalid request content.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-request-content"
}
```

#### User or Password Invalid

This error happens when you try to log in and the user or password is invalid.
-**Sample Error Response:**

```json
{
  "title": "User or password are invalid.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-or-password-are-invalid"
}
```

#### Invalid Username

(400)
_TODO_

- **Sample Error Response:**

```json
{
  "title": "Invalid request content.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-request-content"
}
```

#### Insecure Password

(400)
_TODO_

- **Sample Error Response:**

```json
{
  "title": "Invalid request content.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-request-content"
}
```

#### Token Not Revoked

(403)
_TODO_

- **Sample Error Response:**

```json
{
  "title": "Token not revoked.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/token-not-revoked"
}
```

#### User Not Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "User not found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-not-found"
}
```

#### Rule Not Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Rule not found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/rules-not-found"
}
```

#### User Stats Not Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "User stats not found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-stats-not-found"
}
```

#### User Already Exists

This error happens when you try to create a user and it already exists.

- **Sample Error Response:**

```problem+json
{
  "title": "User already exists.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-already-exists"
}
  ```

#### Same Player

(400)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Same player.",
  "type": ""https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/same-player"
}
```

#### Game Already Finished

(400)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Game already finished.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/game-already-finished"
}
```

#### Impossible Position

(400)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Impossible position.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/impossible-position"
}
```

#### Not Your Turn

(400)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Not your turn.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/not-your-turn"
}
```

#### Invalid Move

(400)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Invalid move.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-move"
}
```

#### Player Not in Game

(401)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Player not in game.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/player-not-in-game"
}
```

#### Game Not Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Game not found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/game-not-found"
}
```

#### No Rules Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "No rules found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/no-rules-found"
}
```

#### Lobby Not Found

(404)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Lobby not found.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/lobby-not-found"
}
```

#### Position Already Occupied

(409)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Position already occupied.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/position-already-occupied"
}
```

#### Internal Server Error

(500)
Internal Server Error

- **Sample Error Response:**

```problem+json
{
  "title": "Internal Server Error",
  "type" : "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/internal-server-error"
}
```

#### Make Move Failed

(500)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Make move failed.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/make-move-failed"
}
```

#### Leave Lobby Failed

(500)
_TODO_

- **Sample Error Response:**

```problem+json
{
  "title": "Leave lobby failed.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/leave-lobby-failed"
}
```
