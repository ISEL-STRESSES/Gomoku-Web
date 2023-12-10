import * as React from 'react';
import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { UserService } from '../../../service/user/UserService';
import { Failure, Success } from '../../../utils/Either';
import { Problem } from '../../../service/media/Problem';
import { useSetUser, useCurrentUser } from "../Authn";
import { getUserName } from '../../../utils/cookieUtils';
import { ConfirmationDialog } from '../../shared/ConfirmationDialog';

type LogoutState =
  | { type: 'confirm' }
  | { type: 'loading' }
  | { type: 'success' }
  | { type: 'error'; message: string };

export function Logout() {
  const [state, setState] = useState<LogoutState>({ type: 'confirm' });
  const currentUser = useCurrentUser();
  const setUser = useSetUser();

  const handleLogout = () => {
    setState({ type: 'loading' });
    UserService.logout()
      .then(res => {
        if (res instanceof Success) {
          setUser(getUserName()); // set user to undefined
          setState({ type: 'success' });
        } else if (res instanceof Failure) {
          const message = res.value instanceof Problem ?
            res.value.title || 'A problem occurred.' :
            res.value.message || 'Logout failed. Please try again.';
          setState({ type: 'error', message });
        }
      });
  };

  if (!currentUser && state.type !== 'success') {
    return <Navigate to="/login" replace={true} />;
  }

  if (state.type === 'success') {
    return <Navigate to="/" replace={true} />;
  }

  switch (state.type) {
    case 'confirm':
      return (
        <ConfirmationDialog
          message={`Are you sure you want to log out, ${currentUser}?`}
          onConfirm={handleLogout}
          confirmButtonText="Confirm Logout"
          cancelButtonText="Cancel"
        />
      );

    case 'loading':
      return <div className="loading">Logging out...</div>;

    case 'error':
      return (
        <div className="error-message">
          {state.message}
          <button onClick={() => setState({ type: 'confirm' })}>Try Again</button>
        </div>
      );

    default:
      return null;
  }
}