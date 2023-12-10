# Gomoku - Frontend Documentation

> This is the documentation for the frontend of the Gomoku project.

## Table of Contents

- [Introduction](#introduction)
- [Code Structure](#code-structure)
- [API Connection](#api-connection)
- [Authentication](#authentication)
- [Conclusion](#conclusion)

## Introduction

The frontend of the Gomoku project is written in TypeScript, using the React framework, it is also a single page application.
The frontend is responsible for the user interface of the application. 
It is also responsible for the communication with the backend.

This application is a client for the Gomoku API, which is documented [here](../../docs/API-doc.md).
For more information about the backend, please refer to the [backend documentation](../jvm/README.md).

## Code Structure

The code is structured in the following way:

- `js`
  - `public/` - The folder with the index HTML file and website icon.
  - `src/`
    - `assets/` - Contains the images and other assets used in the application;
    - `components/` - Contains the React components and pages used in the application;
    - `service/` - Contains the services used in the application; this layer is responsible for the communication
      with the API;
    - `utils/` - The pages of the application
    - `index.css` - The CSS file of the application
    - `index.tsx` - The entry point of the application
    - `router.tsx` - The router for the application

In the `js` folder, there are other files used for the development of the application, like the `package.json` file,
the `tsconfig.json` file and the `webpack.config.js` file.

## API Connection

The API connectivity is done by the service layer.

The media types used in the communication with the API are the following:

* `application/json` - Used in the request bodies;
* `application/problem+json` - Used in the response bodies when an error occurs;
* `application/vnd.siren+json` - Used in the response bodies when the request is successful.

To make the requests, the `fetch` API is used. A `fetchFuntion` function was implemented to make the requests to the API
and to parse the response body to a `Siren` object or to a `Problem` object, depending on the media type of the response.

For each request method (GET, POST, PUT, DELETE), a function that calls the `fechFuntion` function was implemented, to
simplify the code.


## Authentication

The authentication is done using the JWT token. The token is stored in the local storage of the browser.

## Conclusion

