# Gomoku API Documentation

## Table of Contents

- [Gomoku API Documentation](#gomoku-api-documentation)
    - [Table of Contents](#table-of-contents)
    - [Introduction](#introduction)
    - [Modeling the Database](#modeling-the-database)
        - [Conceptual Model](#conceptual-model)
        - [Physical Model](#physical-model)
    - [Application Architecture](#application-architecture)
        - [Controller Layer](#controller-layer)
        - [Service Layer](#service-layer)
        - [Repository Layer](#repository-layer)
        - [Data Representation](#data-representation)
        - [Authentication](#authentication)
    - [Error Handling](#error-handling)
        - [Exception Handling](#exception-handling)
        - [Problem Handling](#problem-handling)
    - [Running the application](#running-the-application)

## [Introduction](#introduction)

The backend server is an API that provides the functionality for the Gomoku game.
It is written in Kotlin using a JVM gradle project.

The JVM application is a simple Spring Boot application, built with Spring Initializr.

To read more about the JVM application structure, see the [Application Architecture](#application-architecture)
section.

The Gomoku game is a two-player strategy board game in which the players take turns in placing a stone of their color.
In a 15x15 or 19x19 board, with the goal of getting five stones in a row, either horizontally, vertically or diagonally.
Lean more about the game [here](https://en.wikipedia.org/wiki/Gomoku).

## [Modeling the Database](#modeling-the-database)

### [Conceptual Model](#conceptual-model)

The following diagram holds the Entity-Relationship model for the information managed by the system.

![ER Diagram](../../docs/diagrams/EA_Model-diagram.drawio.svg)

### [Physical Model](#physical-model)

The physical model of the database is available in [create-tables.sql](src/sql/create-tables.sql).

To implement and manage the database, PostgresSQL was used.

The [`code/sql`](src/sql) folder contains all SQL scripts developed:

[create-tables.sql](src/sql/create-tables.sql) - Creates the database schema;
[insert-test-data.sql](src/sql/insert-test-data.sql) -
Erases the previous data and inserts a test double dataset for testing
purposes;

## [Application Architecture](#application-architecture)

![Application architecture](../../docs/diagrams/Backend-diagram-Page-5.drawio.png)

The application is structured as follows:

- [/domain](src/main/kotlin/gomoku/server/domain) - Contains the domain classes;
- [/http](src/main/kotlin/gomoku/server/http) - Contains the controller layer of the application using Spring Web MVC ;
- [/repository](src/main/kotlin/gomoku/server/repository) - Contains the data access layer of the application using
  JDBI;
- [/services](src/main/kotlin/gomoku/server/services) - Contains the service layer of the application;
- [/validation](src/main/kotlin/gomoku/server/domain/user/PasswordValidationInfo.kt) - Contains a class for the password validation;
- [/utils](src/main/kotlin/gomoku/utils) - Contains utility classes;

### [Controller Layer](src/main/kotlin/gomoku/server/http)

The controller layer is responsible for handling the HTTP requests, processing them and giving back a response, which is
annotated with `@RestController` and `@RequestMapping`, and the methods are annotated with `@GetMapping`
or `@PostMapping`
depending on the HTTP method.
The responses are returned as `Siren Entities` (OutputModels) or `json+problem` (Problems),
depending on the success or failure of the request.

This layer is organized in the following packages:

- [/controllers](src/main/kotlin/gomoku/server/http/controllers) - Contains the controllers for the API;
- [/infra](src/main/kotlin/gomoku/server/http/infra) - Contains the infrastructure classes for the responses and the
  siren specification;
- [/pipeline](src/main/kotlin/gomoku/server/http/pipeline) - Contains the authentication pipeline for the controllers;
- [/responses](src/main/kotlin/gomoku/server/http/responses) - Contains the response templates for the controllers;

We have for controllers:

- [HomeController](src/main/kotlin/gomoku/server/http/controllers/HomeController.kt) - Handles the home requests;
- [GameController](src/main/kotlin/gomoku/server/http/controllers/game/GameController.kt) - Handles the game requests;
- [UserController](src/main/kotlin/gomoku/server/http/controllers/user/UserController.kt) - Handles the user requests;
- [LobbyController](src/main/kotlin/gomoku/server/http/controllers/lobby/LobbyController.kt) - Handles the lobby requests;

### [Service Layer](src/main/kotlin/gomoku/server/services)

The service layer is responsible for managing the business logic of the application, receiving the requests from the
controller layer, processing and sending them to the repository layer, returning the responses to the
controller layer. These responses can be one of two types:

- **Domain model** - If the operation only has one thing that can go wrong, we send to the controller the domain model
  or null;
- **Results** - If the operation has more than one thing that can go wrong, we need to separate them, and we chose to do
  that using the Either monad, that can be either a success or a failure;

Each service is annotated with `@Service`.

This layer is organized in the following packages:

- [/errors](src/main/kotlin/gomoku/server/services/errors) - Contains the errors for the services;
- [/game](src/main/kotlin/gomoku/server/services/game) - Contains the services and results for the game;
- [/user](src/main/kotlin/gomoku/server/services/user) - Contains the services and results for the user;
- [/lobby](src/main/kotlin/gomoku/server/services/lobby) - Contains the services and results for the lobby;

### [Repository Layer](src/main/kotlin/gomoku/server/repository)

The repository layer is responsible for managing the data access of the application, receiving the requests from the
service layer, processing and sending them to the database, returning the responses to the service layer.
The data representation used in these layers is the domain model itself.

This layer uses JDBI to access the database.

This layer is organized in the following packages:

- [/game](src/main/kotlin/gomoku/server/repository/game) - Contains the repositories for the game;
- [/user](src/main/kotlin/gomoku/server/repository/user) - Contains the repositories for the user;
- [/lobby](src/main/kotlin/gomoku/server/repository/lobby) - Contains the repository for the lobby;
- [/jdbi](src/main/kotlin/gomoku/server/repository/jdbi) - Contains the implementation of the transaction
  and transaction manager for JDBI with the mappers needed to convert the objects retried from the database to the
  domain model;

### [Data Representation](#data-representation)

- **Domain Model** - The domain model is the main representation of the data and is used up to the controller layer;
- **Input/Output Model** - The output model is the representation of the data in the HTTP responses, and is used in the
  controller layer to represent the request and response bodies;

### [Authentication](src/main/kotlin/gomoku/server/http/pipeline)

The [`AuthenticationInterceptor`](src/main/kotlin/gomoku/server/http/pipeline/AuthenticationInterceptor.kt)
class implements the `HandlerInterceptor` interface, and is responsible for intercepting the requests and checking if
the user is authenticated, in other words, if the endpoint has a parameter type `AuthenticateUser`, with in turn checks
based on the `Authorization` header if the user is authenticated or not, or by checking if the user is authenticated
based on cookies.

## [Error Handling](#error-handling)

### [Exception Handling](src/main/kotlin/gomoku/server/http/ExceptionHandler.kt)

The [`ExceptionHandler`](src/main/kotlin/gomoku/server/http/ExceptionHandler.kt) class, annotated
with `@ControllerAdvice`,
is responsible for handling the exceptions thrown by the application, and returning the correct response to the client.
This is only used in exceptional cases, where the application throws an exception, and not in the normal flow of the
application, where the application returns a `Problem` object.

### [Problem Handling](src/main/kotlin/gomoku/server/http/controllers/media/Problem.kt)

The [`Problem`](src/main/kotlin/gomoku/server/http/controllers/media/Problem.kt) class is responsible for handling
errors
that are either from the client or the server, and follows the [RFC 7807](https://tools.ietf.org/html/rfc7807) standard.
Our `Problem` implementation only includes the type and title of the problem, but this can be extended to hold more
information, like the detail, instance, etc.

## [Running the application]()

To run the application, you need to have the following installed:

- Gradle
- Java 17

With the Gradle wrapper, you can build the application with the following command on the [jvm folder](./):

```shell
./gradlew build
```