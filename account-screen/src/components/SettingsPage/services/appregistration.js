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

export const getApps = (token) => {
    const url = "http://local.courier.net:8090/api/v1/app/getAll";
    return fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Authorization": "Bearer" + token
        }
    })
        .then(response => {
            if (response.status !== 200 && response.status !== 201)
                throw Error("Error retrieving registered applications");
            if (response.status === 201)
                return null;
            return response.json();
        })
        .catch(error => {
            console.error(error);
            return null;
        })
}
