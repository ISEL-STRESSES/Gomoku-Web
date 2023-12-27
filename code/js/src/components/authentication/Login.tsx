import * as React from 'react';
import { useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { UserService } from '../../service/user/UserService';
import { Failure, Success } from '../../utils/Either';
import { Problem } from '../../service/media/Problem';
import { Alert, IconButton, InputAdornment, Stack, TextField } from '@mui/material';
import { useSetUser } from "./Authn";
import { getUserName } from "../../utils/cookieUtils";
import { Visibility, VisibilityOff } from '@mui/icons-material';

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
  const [showPassword, setShowPassword] = useState(false);
  const location = useLocation();
  const setUser = useSetUser();

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
  };

  if (state.tag === 'redirect') {
    return <Navigate to={location.state?.source?.pathname || '/me'} replace={true} />;
  }

  function handleChange(event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const inputName = event.target.name as 'username' | 'password';
    dispatch({ type: 'edit', inputName, inputValue: event.target.value });
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
          setUser(getUserName())
          dispatch({ type: 'success' });
        } else if (res instanceof Failure) {
          if (res.value instanceof Error) {
            dispatch({ type: 'error', message: res.value.message });
          } else if (res.value instanceof Problem) {
            const problemMessage = res.value.title || 'A problem occurred';
            dispatch({ type: 'error', message: problemMessage });
          } else {
            dispatch({ type: 'error', message: 'An unexpected error occurred' });
          }
        }
      });
  }


  const username = state.tag === 'submitting' ? state.username : state.inputs.username;
  const password = state.tag === 'submitting' ? '' : state.inputs.password;

  return (
    <div id='authDiv'>
      <form onSubmit={handleSubmit} id='authForm'>
        <fieldset disabled={state.tag !== 'editing'} id='authFieldSet'>
          <div>
            <label htmlFor='input'>Username</label>
            <TextField
              id='autbBtnUser'
              className='input'
              type='text'
              name='username'
              value={username}
              onChange={handleChange}
              required
            />
          </div>
          <br/>
          <div>
            <label htmlFor='inputPassword'>Password</label>
            <TextField
              id='autbBtnPass'
              className='input'
              type={showPassword ? 'text' : 'password'}
              name='password'
              value={password}
              onChange={handleChange}
              required
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </div>
          <br/>
          <div>
            <Stack spacing={2} direction='row'>
              <button className='button' type='submit' onClick={() => setSignUp(false)}>
                Log in
              </button>
              <button className='button' type='submit' onClick={() => setSignUp(true)}>
                Sign Up
              </button>
            </Stack>
          </div>
          {state.tag === 'editing' && state.error && (
            <Alert severity='error' className='error-message'>
              {state.error}
            </Alert>
          )}
        </fieldset>
      </form>
    </div>
  );
}
