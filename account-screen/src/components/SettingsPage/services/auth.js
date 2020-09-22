export const authorize = async () => {
    const url = "http://local.courier.net:8090/api/v1/settings/auth";
    return fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("acc.tok"),
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.status !== 200) {
                console.log(response);
                throw Error("Failed to authorize your account.");
            }
            return response.json();
        })
        .catch(error => {
            console.error(error);
            return null;
        })
}

export const refreshAccessToken = async () => {
    const url = `http://local.courier.net:8090/api/v1/token?grant_type=refresh_token&refresh_token=${localStorage.getItem("ref.tok")}`;
    return fetch(url, {
        method: "POST",
        credentials: "include"
    })
        .then(response => {
            if (response.status !== 200)
                throw Error("Failed to refresh access token.");
            return response.json();
        })
        .then(json => {
            console.log(json.token);
            localStorage.setItem("acc.tok", json.token);
            return true;
        })
        .catch(error => {
            console.error(error);
            return false;
        })
}
