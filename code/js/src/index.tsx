import * as React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css'
import { Router } from './router';
import { createTheme } from '@mui/material/styles';

const root = createRoot(document.getElementById('main-div')!); //todo: this is provisional
root.render(<Router />);


export const darkTheme = createTheme({
  palette: {
    mode: 'dark',
  },
});
