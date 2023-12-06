import * as React from 'react';
import { useCurrentUser } from "../authentication/Authn";

export function ShowMe() {
    const currentUser = useCurrentUser()

    return (
        <div>
            <h1>Hello {currentUser}</h1>
        </div>
    );
}
