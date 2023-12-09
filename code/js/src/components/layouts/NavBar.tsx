import { Link, Outlet } from "react-router-dom";
import { useCurrentUser } from "../authentication/Authn";
import * as React from "react";
import UserPhoto from "../../assets/user.png";
import Logo from "../../assets/logo.png";

export function NavBar() {
  return (
    <div>
      <nav>
        <div id="title">
          <Link to="/">
            <img id="image" src={Logo} alt="Gomoku" style={{ height: 'auto', width: '70px' }} />
          </Link>
        </div>
        <input type="checkbox" id="click"/>
        <label htmlFor="click" className="menu-btn">
          <i className="fas fa-bars"></i>
        </label>
        { Nav() }
      </nav>
      <Outlet />
    </div>
  )
}

function Nav() {
  const currentUser = useCurrentUser()
  if (!currentUser)
    return (
      <ol id="navMenu">
        <li><Link to="/about">About</Link></li>
        <li><Link to="/ranking">Ranking</Link></li>
        <li><Link to="/login">Login</Link></li>
      </ol>
    );
  else {
    return ((
      <ol id="navMenu">
        <li><Link to="/about">About</Link></li>
        <li><Link to="/ranking">Ranking</Link></li>
        <li><Link to="/me"><img id="user-image" src={UserPhoto} alt="user"></img></Link></li>
        <li><Link to="/logout">Logout</Link></li>
      </ol>
    ));
  }
}