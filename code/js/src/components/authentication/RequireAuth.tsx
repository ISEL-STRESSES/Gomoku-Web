import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useCurrentUser, useLoading } from "./Authn";
import Loading from '../shared/Loading';

export function RequireAuth({ children }: { children: React.ReactNode }): React.ReactElement {
  const currentUser = useCurrentUser()
  const location = useLocation()
  const loading = useLoading()

  if (loading) {
    return Loading()
  }

  console.log(`currentUser = ${currentUser}`)
  if (currentUser) {
    return <>{children}</>
  } else {
    console.log("redirecting to login")
    return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
  }
}