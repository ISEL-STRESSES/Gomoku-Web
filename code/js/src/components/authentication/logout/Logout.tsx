import * as React from 'react';
import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { UserService } from '../../../service/user/UserService';
import { Failure, Success } from '../../../utils/Either';
import { Problem } from '../../../service/media/Problem';
import './Logout.css';

export function Logout() {
  const [isLoggedOut, setLoggedOut] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    UserService.logout()
      .then(res => {
        if (res instanceof Success) {
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
    <div className="logout-container">
      {errorMessage ? (
        <div className="error-message">
          {errorMessage}
        </div>
      ) : (
        <div className="logout-message">
          Logging out...
        </div>
      )}
    </div>
  );
}
