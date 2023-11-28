import * as React from 'react';
import { createBrowserRouter, Outlet, RouterProvider } from 'react-router-dom';
import { Home } from './Home';
import { About } from './About';
import { AuthnContainer, Logout } from './authentication/Authn';
import { Login } from './authentication/Login';
import { RequireAuth } from "./authentication/RequireAuth";
import { ShowMe } from "./user/ShowMe";

const router = createBrowserRouter([
  {
    'path': '/',
    'element': <AuthnContainer><Outlet /></AuthnContainer>,
    'children': [
      {
        'path': '/',
        'element': <Home />,
        'children': [
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
