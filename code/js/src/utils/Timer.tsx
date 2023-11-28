import * as React from 'react';
import {
    useState,
    useEffect
} from 'react';
import { Navigate, useLocation } from 'react-router-dom';

type TimerProps = {
    initialTime: number,
    restart: boolean
}

export function Timer({ initialTime, restart }: TimerProps): React.ReactElement {
    const location = useLocation();
    const [counter, setCounter] = useState(initialTime);
    const period = 1000;
    useEffect(() => {
        if(restart) setCounter(initialTime);
        const tid = setInterval(_ => setCounter((oldState) => oldState - 1), period);
        return () => {
            clearInterval(tid);
        };
    }, [initialTime, restart, setCounter]);

    if(counter <= 0) return <Navigate to="/" state={{ source: location.pathname }} replace={ true }></Navigate>;

    return (
        <span>{counter}</span>
    );
}
