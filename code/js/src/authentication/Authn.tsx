import * as React from 'react'
import {
  useState,
  createContext,
  useContext,
  useEffect
} from 'react'
import { getCookie } from "../utils/cookieUtils";
import { Navigate } from 'react-router-dom';

export const tokenCookie = 'Gomoku-daw-token-cookie';
export const userInfoCookieName = 'Gomoku-daw-userinfo-name-cookie';
export const userInfoCookieId = 'Gomoku-daw-userinfo-id-cookie';

type UserInfo = {
  id: number,
  name: string
}

type ContextType = {
  user: UserInfo | undefined,
  setUser: (newUser: UserInfo | undefined) => void
}
const LoggedInContext = createContext<ContextType>({
  user: undefined,
  setUser: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
  const id = getCookie(userInfoCookieId);
  const name = getCookie(userInfoCookieName);
  const info = id !== undefined && name !== undefined ? { id: parseInt(id), name } : undefined;

  const [userInfo, setUser] = useState(info);
  console.log(`AuthnContainer: ${name}`)

  return (
    <LoggedInContext.Provider value={{ user: userInfo, setUser: setUser }}>
      {children}
    </LoggedInContext.Provider>
  )
}

export function useCurrentUser() {
  return useContext(LoggedInContext).user
}

export function useSetUser() {
  return useContext(LoggedInContext).setUser
}

export function Logout() {
  const setUser = useSetUser();

  useEffect(() => {
    setUser(undefined);
  });

  return (
    <div>
      <Navigate to={ "/" } replace={ true }/>
    </div>
  );
}