import * as React from 'react';
import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { UserService } from '../../../service/user/UserService';
import { Failure, Success } from '../../../utils/Either';
import { Problem } from '../../../service/media/Problem';
import { useSetUser } from "../Authn";
import { getUserName } from "../../../utils/cookieUtils";

export function Logout() {
  const [isLoggedOut, setLoggedOut] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const setUser = useSetUser()

  useEffect(() => {
    UserService.logout()
      .then(res => {
        if (res instanceof Success) {
          setUser(getUserName()) //This will give undefined
          setLoggedOut(true);
        } else if (res instanceof Failure) {
          const message = res.value instanceof Problem ?
            res.value.title || 'A problem occurred.' :
            res.value.message || 'Logout failed. Please try again.';
          setErrorMessage(message);
        }
      });
  }, []);

  if (isLoggedOut) {
    return <Navigate to="/" replace={true} />;
  }

  return (
    <div className="loading">
      {errorMessage ? (
        <div className="error-message">
          {errorMessage}
        </div>
      ) : (
        <div className="loading-message">
          Logging out...
        </div>
      )}
    </div>
  );
}
