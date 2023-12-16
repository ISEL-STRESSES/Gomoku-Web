//TODO: Use this functions in the places that used to use currentUser to display the username and use id.

export function getUsername(userCookie: string) {
    return userCookie.split(":")[0];
}

export function getUserId(userCookie: string) {
  return userCookie.split(":")[1];
}