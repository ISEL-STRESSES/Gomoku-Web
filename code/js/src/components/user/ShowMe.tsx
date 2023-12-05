import * as React from 'react';
import { getUserName } from '../../utils/cookieUtils';

export function ShowMe() {
    const currentUser = getUserName()

    return (
        <div>
            {`Hello ${currentUser!}!`}
        </div>
    );
}
