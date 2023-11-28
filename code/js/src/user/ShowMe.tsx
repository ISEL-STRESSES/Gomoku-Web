import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { useCurrentUser } from "../authentication/Authn";

export function ShowMe() {
    const navigate = useNavigate();
    const currentUser = useCurrentUser();

    return (
        <div>
            {`Hello ${currentUser!.name}!`}
            <button onClick={() => navigate(-1)}>Back</button>
        </div>
    );
}
