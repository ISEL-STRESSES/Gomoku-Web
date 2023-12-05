import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { getUserName } from '../../utils/cookieUtils';

export function RequireAuth({ children }: { children: React.ReactNode }): React.ReactElement {
  const currentUser = getUserName()
  const location = useLocation()
  console.log(`currentUser = ${currentUser}`)
  if (currentUser) {
    return <>{children}</>
  } else {
    console.log("redirecting to login")
    return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
  }

}