import * as React from 'react';
import { Link, Outlet } from 'react-router-dom';

export function Home() {
  return (
    <div>
      <h1>Home</h1>
      <ol>
        <li><Link to="/me">Me</Link></li>
        <li><Link to="/about">About</Link></li>
      </ol>
      <h3>Child routes</h3>
      <Outlet />
    </div>
  )
}