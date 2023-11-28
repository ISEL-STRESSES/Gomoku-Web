type cookieParams = {
    key: string,
    value: string,
    expire: string | undefined,
    path: string | undefined,
};

function makeCookie(cookieParams: cookieParams) {
    document.cookie = `${cookieParams.key}=${cookieParams.value}; expire=${cookieParams.expire ? cookieParams.expire : Date.now() + 24 * 60 * 60 * 1000}; path=${cookieParams.path ? cookieParams.path : '/'}`;
}

function deleteCookie(cookieName: string) {
    document.cookie = `${cookieName}=; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
}

function getCookie(cookieName: string) {
    const cookieArray = document.cookie.split(";");
    const requestedPair = cookieArray.find(pair => pair.split("=")[0].trim() === cookieName);
    return requestedPair ? requestedPair.substring(requestedPair.indexOf('=') + 1) : undefined;
}

export {
    makeCookie,
    deleteCookie,
    getCookie
};
