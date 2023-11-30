import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSetUser } from './Authn';
import { useState } from "react";

const baseURL = 'http://localhost:8080';

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

export async function authenticate (url:string, username:string, password:string): Promise<{id:number, name:string} | undefined> {
  return fetch(baseURL + url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password }),
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(res => {
      if (res) {
        console.log(res);
        return { id: res.id, name: username };
      } else {
        return undefined;
      }
    })
    .catch(error => {
      console.log(error);
      return undefined;
    });
}

export function Login() {
  console.log('Login');
  const [state, dispatch] = React.useReducer(reduce, { tag: 'editing', inputs: { username: '', password: '' } });
  const [isSignUp, setSignUp] = useState(true);
  const setUser = useSetUser();
  const location = useLocation();
  if (state.tag === 'redirect') {
    return <Navigate to={location.state?.source?.pathname || '/me'} replace={true} />;
  }
  function handleChange(ev: React.FormEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', inputName: ev.currentTarget.name, inputValue: ev.currentTarget.value });
  }
  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    console.log('handleSubmit');
    if (state.tag !== 'editing') {
      return;
    }
    console.log('dispatch submit')
    dispatch({ type: 'submit' });
    const username = state.inputs.username;
    const password = state.inputs.password;
    if (isSignUp) {
      authenticate('/api/users/create', username, password)
        .then(res => {
          if (res) {
            console.log(`setUser(${res})`);
            setUser(res);
            dispatch({ type: 'success' });
          } else {
            dispatch({ type: 'error', message: 'Invalid username or password' });
          }
        })
        .catch(error => {
          dispatch({ type: 'error', message: error.message });
        });
      return;
    }else {
      authenticate('/api/users/token', username, password)
        .then(res => {
          if (res) {
            console.log(`setUser(${res})`);
            setUser(res);
            dispatch({ type: 'success' });
          } else {
            dispatch({ type: 'error', message: 'Invalid username or password' });
          }
        })
        .catch(error => {
          dispatch({ type: 'error', message: error.message });
        });
    }
  }

  const username = state.tag === 'submitting' ? state.username : state.inputs.username
  const password = state.tag === 'submitting' ? "" : state.inputs.password

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
