export const registerApp = async (name, redirectUri, token) => {
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

export const getApps = async (token) => {
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

export const updateApp = async (clientId, name, redirectUri, token) => {
    const url = "http://local.courier.net:8090/api/v1/app/update";
    return fetch(url, {
        method: "PUT",
        credentials: "include",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ name: name, redirectUri: redirectUri, clientId: clientId })
    })
        .then(response => {
            if (response.status !== 200)
                throw Error("Error updating existing application.");
            return true;
        })
        .catch(error => {
            console.error(error);
            return false;
        })
}
