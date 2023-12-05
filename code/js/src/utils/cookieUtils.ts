function getCookie(cookieName: string) {
    const cookieArray = document.cookie.split(";");
    const requestedPair = cookieArray.find(pair => pair.split("=")[0].trim() === cookieName);
    return requestedPair ? requestedPair.substring(requestedPair.indexOf('=') + 1) : undefined;
}

export function getUserName() {
    return getCookie("usernameCookie");
}