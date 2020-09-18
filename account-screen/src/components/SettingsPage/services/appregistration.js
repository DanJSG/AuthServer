export const registerApp = (name, redirectUri, token) => {
    const url = "http://local.courier.net:8090/api/v1/app/register";
    return fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ redirectUri: redirectUri, name: name })
    })
        .then(response => {
            console.log(response);
        })
        .catch(error => {
            console.error(error);
        });
}
