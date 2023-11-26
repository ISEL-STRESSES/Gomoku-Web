import * as React from 'react';
import { createBrowserRouter, Outlet, RouterProvider } from 'react-router-dom';
import { Home } from './Home';
import { About } from './About';
import { Me } from './Me';
import { AuthnContainer } from './authentication/Authn';
import { RequireAuthn } from './authentication/RequireAuthn';
import { Login } from './authentication/Login';

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
            'path': '/about',
            'element': <About />,
          },
          {
            'path': '/login',
            'element': <Login />,
          },
        ],
      },
      {
        'path': '/me',
        'element': <RequireAuthn><Me /></RequireAuthn>,
      },
      {
        'path': '/about',
        'element': <About />,
      }
    ],
  },
]);

export function Router() {
  return (
    <RouterProvider router={router} />
  );
}
