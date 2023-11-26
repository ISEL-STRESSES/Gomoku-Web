import * as React from 'react';
import { useCurrentUser } from './authentication/Authn';

export function Me() {
  const currentUser = useCurrentUser()
  return (
    <div>
      {`Hello ${currentUser}`}
    </div>
  )
}