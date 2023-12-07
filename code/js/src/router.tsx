import * as React from 'react';
import { createBrowserRouter, Outlet, RouterProvider } from 'react-router-dom';
import { Home } from "./components/Home";
import { NavBar } from "./components/layouts/NavBar";
import { About } from './components/About';
import { Login } from './components/authentication/Login';
import { RequireAuth } from "./components/authentication/RequireAuth";
import { ShowMe } from "./components/user/ShowMe";
import { Logout } from './components/authentication/logout/Logout';
import { Ranking } from "./components/Ranking";
import { AuthnContainer } from "./components/authentication/Authn";
import { UserDetails } from "./components/user/UserDetails";
import GameplayMenu from "./components/game/GameplayMenu";
import { CreateGame } from "./components/game/CreateGame";
import { Lobbies } from "./components/game/Lobbies";

const router = createBrowserRouter([
  {
    'path': '/',
    'element': <AuthnContainer><Outlet /></AuthnContainer>,
    'children': [
      {
        'path': '/',
        'element': <NavBar />,
        'children': [
          {
            'path': '/',
            'element': <Home />,
          },
          {
            'path': '/login',
            'element': <Login />,
          },
          {
            'path': '/about',
            'element': <About />,
          },
          {
            "path": "/me",
            "element": <RequireAuth><ShowMe /></RequireAuth>
          },
          {
            "path": "/logout",
            "element": <Logout />
          },
          {
            'path': '/ranking',
            'element': <Ranking />,
          },
          {
            'path': '/users/:userId',
            'element': <UserDetails />
          },
          {
            'path': '/gameplay-menu',
            'element': <RequireAuth><GameplayMenu/></RequireAuth>,
          },
          {
            'path': '/create-game',
            'element': <RequireAuth><CreateGame/></RequireAuth>,
          },
          {
            'path': '/lobby',
            'element': <RequireAuth><Lobbies/></RequireAuth>,
          }
        ],
      },
    ],
  },
]);

export function Router() {
  return (
    <RouterProvider router={router} />
  );
}
