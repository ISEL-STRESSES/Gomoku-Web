import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useState } from "react";
import { UserService } from '../../service/user/UserService';
import { Failure, Success } from '../../utils/Either';
import { Problem } from '../../service/media/Problem';

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string } }
  | { tag: 'submitting'; username: string }
  | { tag: 'redirect' };

type Action =
  | { type: 'edit'; inputName: string; inputValue: string }
  | { type: 'submit' }
  | { type: 'error'; message: string }
  | { type: 'success' };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      if (action.type === 'edit') {
        return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.inputName]: action.inputValue } };
      } else if (action.type === 'submit') {
        return { tag: 'submitting', username: state.inputs.username };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'submitting':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else if (action.type === 'error') {
        return { tag: 'editing', error: action.message, inputs: { username: state.username, password: '' } };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function Login() {
  console.log('Login');
  const [state, dispatch] = React.useReducer(reduce, { tag: 'editing', inputs: { username: '', password: '' } });
  const [isSignUp, setSignUp] = useState(true);
  const location = useLocation();

  if (state.tag === 'redirect') {
    return <Navigate to={location.state?.source?.pathname || '/me'} replace={true} />;
  }

  function handleChange(ev: React.FormEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', inputName: ev.currentTarget.name, inputValue: ev.currentTarget.value });
  }

  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    if (state.tag !== 'editing') {
      return;
    }
    dispatch({ type: 'submit' });

    const authFunction = isSignUp ? UserService.signUp : UserService.login;
    authFunction(state.inputs.username, state.inputs.password)
      .then(res => {
        if (res instanceof Success) {
          dispatch({ type: 'success' });
        } else if (res instanceof Failure) {
          // Check if the failure is due to an Error or a Problem
          if (res.value instanceof Error) {
            // Handle the Error case
            dispatch({ type: 'error', message: res.value.message });
          } else if (res.value instanceof Problem) {
            // Handle the Problem case
            // Adjust this based on the structure of your Problem class
            const problemMessage = res.value.title || 'A problem occurred';
            dispatch({ type: 'error', message: problemMessage });
          } else {
            // Generic error handling if the failure type is unknown
            dispatch({ type: 'error', message: 'An unexpected error occurred' });
          }
        }
      });
  }



  const username = state.tag === 'submitting' ? state.username : state.inputs.username;
  const password = state.tag === 'submitting' ? "" : state.inputs.password;

  return (
    <div id="authDiv">
      <form onSubmit={handleSubmit} id="authForm">
        <fieldset disabled={state.tag !== 'editing'} id="authFieldSet">
          <div>
            <label htmlFor="input">Username</label>
            <input id="autbBtnUser" className="input" type="text" name="username" value={username} onChange={handleChange} required />
          </div>
          <div>
            <label htmlFor="input">Password</label>
            <input id="autbBtnPass" className="input" type="password" name="password" value={password} onChange={handleChange} required />
          </div>
          <div>
            <button className="button" type="submit" onClick={() => setSignUp(false)}>
              Log in
            </button>
            <button className="button" type="submit" onClick={() => setSignUp(true)}>
              Sign Up
            </button>
          </div>
        </fieldset>
      </form>
    </div>
  );
}
