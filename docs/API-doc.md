# Gomoku API

> version: 0.1.1

Web-based system that allows multiple players to play the Gomoku game, a strategy board game for two players, who take
turns placing a stone of their color on an empty intersection. This API facilitates game creation, user management,
and gameplay.

## Table of Contents

- [Introduction](#introduction)
- [Pagination](#pagination)
- [Authentication](#authentication)
- [Endpoints](#endpoints)
    - [Home](#home)
        - [Get Home](#get-home)
    - [Users](#users)
        - [Create a New User](#create-a-new-user)
        - [Logs a User In](#log-a-user-in)
        - [Logout a User](#logout-a-user)
        - [Create a token for a user](#create-a-token-for-a-user)
        - [Get a user home page](#get-a-user-home-page)
        - [Get the ranking of the users for a given rule](#get-the-ranking-of-the-users-for-a-given-rule)
        - [Get the ranking of a user for a given rule](#get-the-ranking-of-a-user-for-a-given-rule)
        - [Get All stats of a user](#get-all-stats-of-a-user)
        - [Get the user with the given id](#get-the-user-with-the-given-id)
    - [Games](#games)
        - [Get the Finished games](#get-finished-games)
        - [Get the available rules](#get-game-rules)
        - [Get the details of a game](#get-game-details)
        - [Makes a move in a game](#make-a-move)
        - [Gets the player id of the current turn](#get-current-turn-player-id)
        - [Starts the matchmaking process](#start-the-matchmaking-process)
        - [Forfeit a game](#forfeit-a-game)
    - [Lobbies](#lobbies)
      - [Get all lobbies](#get-all-lobbies)
      - [Create a lobby](#create-a-lobby)
      - [Join a lobby](#join-a-lobby)
      - [Leave the lobby](#leave-a-lobby)
      - [Get the details of a lobby](#get-the-details-of-a-lobby)
- [Types](#types)
    - [Parameters](#parameters)
        - [Password](#password)
        - [Username](#username)
        - [Position](#position)
        - [RuleId](#ruleid)
        - [UserId](#userid)
        - [LobbyId](#lobbyid)
        - [GameId](#gameid)
        - [Offset](#offset)
        - [Limit](#limit)
    - [Success Types](#success-types)
        - [Home](#home-1)
        - [User Created](#user-created)
        - [User Logged in](#user-logged-in)
        - [User Logged out](#user-logged-out)
        - [User Home](#user-home)
        - [User](#user)
        - [Users Stats](#users-stats)
        - [User Stats](#user-stats)
        - [Available Rules](#available-rules)
        - [Finished Games](#finished-games)
        - [Game Details](#game-details)
        - [Current Turn Player Id](#current-turn-player-id)
        - [Lobby Details](#lobby-details)
        - [Lobby List](#lobby-list)
    - [Error Types](#error-types)
        - [Bad Request](#bad-request)
        - [User or Password Invalid](#user-or-password-invalid)
        - [Invalid Username](#invalid-username)
        - [Insecure Password](#insecure-password)
        - [Token Not Revoked](#token-not-revoked)
        - [Unauthorized](#unauthorized)
        - [User Not Found](#user-not-found)
        - [Rule Not Found](#rule-not-found)
        - [User Stats Not Found](#user-stats-not-found)
        - [User Already Exits](#user-already-exists)
        - [Same Player](#same-player)
        - [Game Already Finished](#game-already-finished)
        - [Impossible Position](#impossible-position)
        - [Not Your Turn](#not-your-turn)
        - [Invalid Move](#invalid-move)
        - [Player Not in Game](#player-not-in-game)
        - [Game Not Found](#game-not-found)
        - [No Rules Found](#no-rules-found)
        - [Lobby Not Found](#lobby-not-found)
        - [User Already in Lobby](#user-already-in-lobby)
        - [Position Already Occupied](#position-already-occupied)
        - [Internal Server Error](#internal-server-error)
        - [Make Move Failed](#make-move-failed)
        - [Leave Lobby Failed](#leave-lobby-failed)

## Introduction

For the purpose of the URLs in this document, the base URL is `https://localhost:8080/api/`.

The Siren media type is used to represent the resources.
It is a media type following a hypermedia specification for representing entities.
It is a JSON-based format.
Optimized for HTTP. Siren is designed to be simple, extendable, and consistent.
The Internet Media Type for Siren is `application/vnd.siren+json`.

As for the Errors the media type is `application/problem+json` and it follows the
[RFC 7807](https://tools.ietf.org/html/rfc7807).

## Pagination

The pagination is done with the header `Link` and the following format:

There is the relation of `self` (current page), `prev` and `next` (previous and next page).

## Authentication

There are requests that need authentication and to those requests you need to add the header `Authorization` with a
Bearer Token or cookies.

## Endpoints

### Home

#### Get Home

Retrieve basic information or status of the Gomoku API.

- **URL:** `/api/`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Home](#home-1)
- **Error Response:**
    - **Content:**
        - `application/problem+json`
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
  ```bash
  curl https://localhost:8080/api/
    ```

### Users

#### Create a new user

Create a new user.

- **URL:** `/api/users/create`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User Created](#user-created)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Bad Request](#bad-request)
            - [Invalid Username](#invalid-username)
            - [Insecure Password](#insecure-password)
            - [User Already Exists](#user-already-exists)
            - [Internal Server Error](#internal-server-error)

- **Sample Call:**
    ```bash
    curl -X POST -d "username=foo&password=bar" https://localhost:8080/api/users/create
    ```

#### Log a User in

Log a user in.

- **URL:** `/api/users/token`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
- **Content:**
    - `application/vnd.siren+json`
        - [User Logged in](#user-logged-in)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [User or Password Invalid](#user-or-password-invalid)
            - [Internal Server Error](#internal-server-error)

#### Logout a User

Log a user out. **Requires authentication**

- **URL:** `/api/users/logout`
- **Method:** `POST`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User Logged out](#user-logged-out)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Token Not Revoked](#token-not-revoked)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/users/logout
    ```

#### Get a User Home Page

Retrieve user-specific home page. **Requires authentication**

- **URL:** `/api/users/me`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User Home](#user-home)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/me
    ```

#### Get the ranking of the users for a given rule

Get the ranking of the users for a given rule.

- **URL:** `/api/users/ranking/{ruleId}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [RuleId](#ruleid)
    - **Optional:**
        - [Username](#username)
        - [Offset](#offset)
        - [Limit](#limit)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Users Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Rule Not Found](#rule-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/ranking/1?username=foo
    ```

#### Get the ranking of a user for a given rule

Get the ranking of a user for a given rule.

- **URL:** `/api/users/ranking/{rukeId}/{userId}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [RuleId](#ruleid)
        - [UserId](#userid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [User Not Found](#user-not-found)
            - [Rule Not Found](#rule-not-found)
            - [User Stats Not Found](#user-stats-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/ranking/2/1
    ```

#### Get all stats of a user

Get the whole stats of a user. **Requires authentication** (dunno)

- **URL:** `/api/users/stats/{userId}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [UserId](#userid)
- **Success Response:**
- **Content:**
    - `application/vnd.siren+json`
        - [User Stats](#user-stats)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [User Not Found](#user-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/stats/1
    ```

#### Create a token for a user

Create a token for a user.

- **URL:** `/api/users/token`
- **Method:** `POST`
- **Payload Params:**
    - **Required:**
        - [Username](#username)
        - [Password](#password)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User Logged in](#user-logged-in)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [User or Password Invalid](#user-or-password-invalid)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST -d "username=foo&password=bar" https://localhost:8080/api/users/token
    ```

#### Get the user with the given id

Get the user with the given id.

- **URL:** `/api/users/{userId}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [UserId](#userid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [User](#user)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [User Not Found](#user-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/users/1
    ```

### Games

#### Get Finished Games

Retrieve a list of finished games.

- **URL:** `/api/game/`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Finished Games](#finished-games)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/
    ```

#### Get Game Rules

Retrieves available game rules.

- **URL:** `/api/game/rules`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Available Rules](#available-rules)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [No Rules Found](#no-rules-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/rules
    ```

#### Get Game Details

Retrieve the details of a game. **Requires authentication**
- **URL:** `/api/game/{id}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [GameId](#gameid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Game Details](#game-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Game Not Found](#game-not-found)
            - [User Not Found](#user-not-found)
            - [Player Not in Game](#player-not-in-game)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/1
    ```

#### Make a Move

Makes a move in a game. **Requires authentication**

- **URL:** `/api/game/{id}/play`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [GameId](#gameid)
- **Query Params:**
    - **Required:**
        - [Position](#position)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Game Details](#game-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Game Already Finished](#game-already-finished)
            - [Impossible Position](#impossible-position)
            - [Not Your Turn](#not-your-turn)
            - [Invalid Move](#invalid-move)
            - [Unauthorized](#unauthorized)
            - [Player Not in Game](#player-not-in-game)
            - [Game Not Found](#game-not-found)
            - [User Not Found](#user-not-found)
            - [Position Already Occupied](#position-already-occupied)
            - [Make Move Failed](#make-move-failed)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/1/play?x=1&y=1
    ```

#### Get Current Turn Player Id

Retrieves the player id of the current turn. **Requires authentication**

- **URL:** `/api/game/{id}/turn`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [GameId](#gameid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Current Turn Player id](#current-turn-player-id)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Game Already Finished](#game-already-finished)
            - [Unauthorized](#unauthorized)
            - [Game Not Found](#game-not-found)
            - [Player Not in Game](#player-not-in-game)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/game/1/turn
    ```

#### Start the Matchmaking Process

Starts the matchmaking process. **Requires authentication**

- **URL:** `/api/game/start/{ruleId}`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [RuleId](#ruleid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Same Player](#same-player)
            - [Unauthorized](#unauthorized)
            - [Leave Lobby Failed](#leave-lobby-failed)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/start/1
    ```

#### Forfeit a Game

Forfeit a game. **Requires authentication**

- **URL:** `/api/game/{gameId}/forfeit`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [GameId](#gameid)
- **Success Response:**
- **Content:**
    - `application/vnd.siren+json`
        - [Game Details](#game-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Game Already Finished](#game-already-finished)
            - [Game Not Found](#game-not-found)
            - [User Not Found](#user-not-found)
            - [Player Not in Game](#player-not-in-game)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/1/forfeit
    ```

### Lobbies

#### Get All Lobbies

Retrieves all lobbies. **Requires authentication**

- **URL:** `/api/lobby/lobbies`
- **Method:** `GET`
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby List](#lobby-list)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/lobby/lobbies
    ```

#### Create a Lobby

Create a lobby. **Requires authentication**

- **URL:** `/api/lobby/create/{ruleId}`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [RuleId](#ruleid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/lobby/create/1
    ```

#### Join a Lobby

Join a player in a lobby. **Requires authentication**

- **URL:** `/api/lobby/{lobbyId}/join`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [LobbyId](#lobbyid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Lobby Not Found](#lobby-not-found)
            - [User already in Lobby](#user-already-in-lobby)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/lobby/1/join
    ```

#### Leave a Lobby

Leaves a lobby. **Requires authentication**

- **URL:** `/api/lobby/{lobbyId}/leave`
- **Method:** `POST`
- **Path Params:**
    - **Required:**
        - [LobbyId](#lobbyid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Lobby Not Found](#lobby-not-found)
            - [User Not in Lobby](#user-not-found)
            - [Leave Lobby Failed](#leave-lobby-failed)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl -X POST https://localhost:8080/api/game/1/leave
    ```

#### Get the Details of a Lobby

Retrieve the details of a lobby. **Requires authentication**

- **URL:** `/api/lobby/{lobbyId}`
- **Method:** `GET`
- **Path Params:**
    - **Required:**
        - [LobbyId](#lobbyid)
- **Success Response:**
    - **Content:**
        - `application/vnd.siren+json`
            - [Lobby Details](#lobby-details)
- **Error Responses:**
    - **Content:**
        - `application/problem+json`
            - [Unauthorized](#unauthorized)
            - [Lobby Not Found](#lobby-not-found)
            - [Internal Server Error](#internal-server-error)
- **Sample Call:**
    ```bash
    curl https://localhost:8080/api/lobby/1
    ```

## Types

### Parameters

#### Password

The password is a string with a length between 6 and 30 characters
It has to contain at least one lowercase letter, one uppercase letter, one digit and one special character.

#### Username

The username is a string with a length between 3 and 50 characters, consisting solely of non-whitespace characters.

#### Position

The Position has two parts, the row(x) and the column(y); both are positive integers.
The row has to be between 1 and the board size, and the column has to be between one and the board size.
The rule defines the board size.

#### RuleId

Only positive integers are allowed.
The rule has to exist.

#### UserId

Only positive integers are allowed.
The user has to exist.

#### LobbyId

Only positive integers are allowed.
The lobby has to exist.

#### GameId

Only positive integers are allowed.
The game has to exist.

#### Offset

Only positive integers are allowed.

#### Limit

Only positive integers are allowed.

### Success Types

#### Home

The application home page has the server info and the authors' info with their socials.

- **Sample Success Response:**

```vnd.siren+json
{
    "class":[
        "home"
    ],
    "properties":{
        "version":"1.0.2",
        "authors":[
            {
                "studentID":48335,
                "name":"Rodrigo Correia",
                "email":"A48335@alunos.isel.pt",
                "socials":[
                    {
                        "name":"GitHub",
                        "url":"https://github.com/RodrigoHCorreia"
                    }
                ]
            },
            {
                "studentID":48331,
                "name":"André Matos",
                "email":"A48331@alunos.isel.pt",
                "socials":[
                    {
                        "name":"GitHub",
                        "url":"https://github.com/Matos16"
                    }
                ]
            },
            {
                "studentID":48253,
                "name":"Carlos Pereira",
                "email":"A48253@alunos.isel.pt",
                "socials":[
                    {
                        "name":"GitHub",
                        "url":"https://github.com/Sideghost"
                    }
                ]
            }
        ]
    },
    "entities":[],
    "actions":[
        {
            "name":"signup",
            "title":"Sign up",
            "method":"POST",
            "href":"/api/users/create",
            "type":"application/vnd.siren+json",
            "fields":[
                {
                    "name":"username",
                    "type":"text",
                    "value":null
                },
                {
                    "name":"password",
                    "type":"password",
                    "value":null
                }
            ]
        },
        {
            "name":"login",
            "title":"Login",
            "method":"POST",
            "href":"/api/users/token",
            "type":"application/vnd.siren+json",
            "fields":[
                {
                    "name":"username",
                    "type":"text",
                    "value":null
                },
                {
                    "name":"password",
                    "type":"password",
                    "value":null
                }
            ]
        },
        {
            "name":"logout",
            "title":"Logout",
            "method":"POST",
            "href":"/api/users/logout",
            "type":"application/vnd.siren+json",
            "fields":[]
        },
        {
            "name":"search-ranking",
            "title":"Search ranking",
            "method":"GET",
            "href":"/api/users/ranking/1?limit=10",
            "type":"application/vnd.siren+json",
            "fields":[
                {
                    "name":"ruleId",
                    "type":"number",
                    "value":"1"
                },
                {
                    "name":"search",
                    "type":"text",
                    "value":null
                },
                {
                    "name":"limit",
                    "type":"number",
                    "value":"10"
                },
                {
                    "name":"offset",
                    "type":"number",
                    "value":null
                }
            ]
        }
    ],
    "links":[
        {
            "rel":["self"],
            "href":"/api/"
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
            "href": "/api/users/create"
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
            "href": "/api/users/token"
        }
    ],
    "actions": []
}
```

#### User Logged out

- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "logout"
    ],
    "properties": "User logged out.",
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/logout"
        }
    ],
    "actions": []
}
```

#### User Home

- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "user"
    ],
    "properties": {
        "id": 27,
        "username": "Rod21",
        "token": "PratQUvC8439q0vqI_1FjCpbVpUhAn_SrANtMf5eOpU="
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/me"
        }
    ],
    "actions": []
}
```

#### User

- **Sample Response:**
```vnd.siren+json
{
    "class": [
        "user"
    ],
    "properties": {
        "id": 536,
        "username": "xpto5",
        "token": "KEkqFztldrzS9bxuhMlLIhLqIOhEqUlmjNO09JxTuoQ="
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/me"
        }
    ],
    "actions": []
}
```

#### Users Stats

- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "user-ranking-search"
    ],
    "properties": {
        "userData": [
            {
                "id": 24,
                "username": "Sofia",
                "ruleId": 2,
                "gamesPlayed": 1,
                "elo": 1520
            },
            {
                "id": 2,
                "username": "user2",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 3,
                "username": "user3",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 4,
                "username": "user4",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 5,
                "username": "user5",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 6,
                "username": "user6",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 1,
                "username": "user1",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 8,
                "username": "user8",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 9,
                "username": "user9",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            },
            {
                "id": 10,
                "username": "user10",
                "ruleId": 2,
                "gamesPlayed": 5,
                "elo": 1500
            }
        ],
        "ruleId": 2,
        "search": "",
        "limit": 10,
        "offset": 0,
        "total": 10
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/ranking/2?username=&limit=10&offset=0"
        },
        {
            "rel": [
                "next"
            ],
            "href": "/api/users/ranking/2?username=&limit=10&offset=10"
        },
        {
            "rel": [
                "previous"
            ],
            "href": "/api/users/ranking/2?username=&limit=10&offset=0"
        }
    ],
    "actions": []
}
```

#### User Stats
- **Sample Response:**

```vnd.siren+json
{
    "class": [
        "user-stats-search"
    ],
    "properties": {
        "userId": 537,
        "username": "TestUser1",
        "userRuleStats": []
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/stats/537"
        }
    ],
    "actions": []
}
```

#### Available Rules

- **Sample Response:**

```vnd.siren+json
{
  "class": [
    "rules"
  ],
  "properties": {
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
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/game/rules"
    }
  ],
  "actions": []
}
```

#### Finished Games

- **Sample Response:**

```vnd.siren+json
{
  "class": [
    "game"
  ],
  "properties": {
    "finishedGames": [
      {
        "id": 12,
        "playerBlack": 24,
        "playerWhite": 23,
        "rule": {
          "ruleId": 2,
          "boardSize": 19,
          "variant": "STANDARD",
          "openingRule": "FREE"
        },
        "moves": {
          "boardSize": 19,
          "orderOfMoves": [
            {
              "position": {
                "x": 0,
                "y": 2
              },
              "color": "BLACK"
            },
            {
              "position": {
                "x": 4,
                "y": 18
              },
              "color": "WHITE"
            },
            {
              "position": {
                "x": 0,
                "y": 0
              },
              "color": "BLACK"
            },
            {
              "position": {
                "x": 4,
                "y": 17
              },
              "color": "WHITE"
            },
            {
              "position": {
                "x": 0,
                "y": 1
              },
              "color": "BLACK"
            },
            {
              "position": {
                "x": 4,
                "y": 14
              },
              "color": "WHITE"
            },
            {
              "position": {
                "x": 0,
                "y": 3
              },
              "color": "BLACK"
            },
            {
              "position": {
                "x": 4,
                "y": 12
              },
              "color": "WHITE"
            },
            {
              "position": {
                "x": 0,
                "y": 4
              },
              "color": "BLACK"
            }
          ]
        },
        "gameOutcome": "BLACK_WON",
        "turn": null,
        "type": "FINISHED"
      }
    ]
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/game/"
    }
  ],
  "actions": []
}
```

#### Game Details

- **Sample Response:**
```vnd.siren+json
{
    "class": [
        "game"
    ],
    "properties": {
        "id": 372,
        "playerBlack": 538,
        "playerWhite": 537,
        "rule": {
            "ruleId": 2,
            "boardSize": 19,
            "variant": "STANDARD",
            "openingRule": "FREE"
        },
        "moves": {
            "boardSize": 19,
            "orderOfMoves": []
        },
        "gameOutcome": null,
        "turn": "BLACK",
        "type": "ONGOING"
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/game/372"
        }
    ],
    "actions": []
}
```

#### Current Turn Player Id

- **Sample Response:**
```vnd.siren+json
{
    "class": [
        "game"
    ],
    "properties": {
        "turn": 538
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/game/538/turn"
        }
    ],
    "actions": []
}
```

#### Lobby Details

- **Sample Response:**
```vnd.siren+json
{
    "class": [
        "game"
    ],
    "properties": {
        "isGame": true,
        "id": 372
    },
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/game/372"
        }
    ],
    "actions": []
}
```

#### Lobby List

- **Sample Response:**
```vnd.siren+json
{
    "class": [
        "get-lobbies"
    ],
    "properties": {
        "lobbies": [
            {
                "id":701,
                "rule": {
                    "ruleId":1,
                    "boardSize":"X15",
                    "type":"StandardRules"
                },
                "userId":1387
            },
            {
                "id":702,
                "rule": {
                    "ruleId":2,
                    "boardSize":"X19",
                    "type":"StandardRules"
                },
                "userId":1388
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
    "actions": []
}
```

### Error Types

#### Bad Request

This error happens when, for example, you try to create a user and the username or password is invalid.

- **Sample Error Response:**

```problem+json
{
  "title": "Invalid request content.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-request-content"
}
```

#### User or Password Invalid

This error happens when you try to log in and the user or password is invalid.

-**Sample Error Response:**

```problem+json
{
  "title": "User or password are invalid.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-or-password-are-invalid"
}
```

#### Invalid Username

- **Sample Error Response:**

```problem+json
{
  "title": "Invalid username format.",
  "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-username"
}
```

#### Insecure Password

- **Sample Error Response:**

```problem+json
{
    "title": "The provided password is insecure.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/insecure-password"
}
```

#### Unauthorized

- **Sample Error Response:**
    
```problem+json
{
    "title": "Unauthorized.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/unauthorized"
}
```

#### Token Not Revoked

- **Sample Error Response:**

```problem+json
{
    "title": "Token not revoked.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/token-not-revoked"
}
```

#### User Not Found

- **Sample Error Response:**

```problem+json
{
    "title": "User not found.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-not-found"
}
```

#### Rule Not Found

- **Sample Error Response:**

```problem+json
{
    "title": "Rule not found.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/rules-not-found"
}
```

#### User Stats Not Found

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

- **Sample Error Response:**

```problem+json
{
    "title": "Same player.",
    "type": ""https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/same-player"
}
```

#### Game Already Finished

- **Sample Error Response:**

```problem+json
{
    "title": "Game already finished.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/game-already-finished"
}
```

#### Impossible Position

- **Sample Error Response:**

```problem+json
{
    "title": "Impossible position.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/impossible-position"
}
```

#### Not Your Turn

- **Sample Error Response:**

```problem+json
{
    "title": "Not your turn.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/not-your-turn"
}
```

#### Invalid Move

- **Sample Error Response:**

```problem+json
{
    "title": "Invalid move.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/invalid-move"
}
```

#### Player Not in Game

- **Sample Error Response:**

```problem+json
{
    "title": "Player not in game.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/player-not-in-game"
}
```

#### Game Not Found

- **Sample Error Response:**

```problem+json
{
    "title": "Game not found.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/game-not-found"
}
```

#### No Rules Found

- **Sample Error Response:**

```problem+json
{
    "title": "No rules found.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/no-rules-found"
}
```

#### Lobby Not Found

- **Sample Error Response:**

```problem+json
{
    "title": "Lobby not found.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/lobby-not-found"
}
```

#### User Already in Lobby

- **Sample Error Response:**

```problem+json
{
    "title": "User already in lobby.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/user-already-in-lobby"
}
```

#### Position Already Occupied

- **Sample Error Response:**

```problem+json
{
    "title": "Position already occupied.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/position-already-occupied"
}
```

#### Internal Server Error

Internal Server Error

- **Sample Error Response:**

```problem+json
{
    "title": "Internal Server Error",
    "type" : "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/internal-server-error"
}
```

#### Make Move Failed

- **Sample Error Response:**

```problem+json
{
    "title": "Make move failed.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/make-move-failed"
}
```

#### Leave Lobby Failed

- **Sample Error Response:**

```problem+json
{
    "title": "Leave lobby failed.",
    "type": "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/leave-lobby-failed"
}
```
