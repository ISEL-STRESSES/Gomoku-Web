import * as React from 'react';
import { createBrowserRouter, Outlet, RouterProvider } from 'react-router-dom';
import { Home } from './components/Home';
import { About } from './components/About';
import { Login } from './components/authentication/Login';
import { RequireAuth } from "./components/authentication/RequireAuth";
import { ShowMe } from "./components/user/ShowMe";
import { Logout } from './components/authentication/logout/Logout';

const router = createBrowserRouter([
  {
    'path': '/',
    'element': <Outlet />,
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
