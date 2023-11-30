import * as React from 'react';
import { useCurrentUser } from "../authentication/Authn";

export function ShowMe() {
    const currentUser = useCurrentUser();

    return (
        <div>
            {`Hello ${currentUser!.name}!`}
        </div>
    );
}
