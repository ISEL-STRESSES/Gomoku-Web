import * as React from 'react'
import {
  useState,
  createContext,
  useContext, useEffect,
} from 'react';
import { getUserName } from '../../utils/cookieUtils';

type ContextType = {
  user: string | undefined,
  setUser: (v: string | undefined) => void,
  loading: boolean
}
const LoggedInContext = createContext<ContextType>({
  user: undefined,
  setUser: () => { },
  loading: true
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | undefined>(undefined);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const username = getUserName();
    if (username) {
      setUser(username);
    }
    setLoading(false);
  }, []);

  console.log(`AuthnContainer: ${user}`)
  return (
    <LoggedInContext.Provider value={{ user: user, setUser: setUser, loading: loading }}>
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

export function useLoading() {
  const context = useContext(LoggedInContext);
  return context.loading;
}