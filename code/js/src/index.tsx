import * as React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css'
import { Router } from './router';

const root = createRoot(document.getElementById('main-div')!); //todo: this is provisional
root.render(<Router />);
