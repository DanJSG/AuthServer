const requestRefreshToken = (params) => {
    const url = `http://local.courier.net:8080/api/v1/token` +
                `?client_id=${params.client_id}` + 
                `&state=${params.state}` +
                `&code=${params.code}` +
                `&redirect_uri=${params.redirect_uri}` +
                `&code_verifier=${params.code_verifier}` +
                `&grant_type=authorization_code`;
    fetch(url, {
        method: "POST",
        credentials: "include"
    })
    .then((response) => {
        if(response.status !== 200) {
            console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
            console.log(response);
            return;
        }
        return response.json();
    })
    .then((json) => {
        console.log(json);
        console.log(json.token);
        localStorage.setItem("ref.tok", json.token);
        requestAccessToken(params.client_id, json.token);
    })
    .catch((error) => {
        console.log(error);
    })
}

const requestAccessToken = (client_id, refresh_token) => {
    console.log("Requesting access token with: " + refresh_token);
    const url = `http://local.courier.net:8080/api/v1/token` +
                `?client_id=${client_id}` + 
                `&refresh_token=${refresh_token}` + 
                `&grant_type=refresh_token`;
    fetch(url, {
        method: "POST",
        credentials: "include"
    })
    .then((response) => {
        if(response.status !== 200) {
            console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
            console.log(response);
            return;
        }
        return response.json();
    })
    .then((json) => {
        console.log(json);
        console.log(json.token);
        localStorage.setItem("acc.tok", json.token);
        window.location.href = "http://local.courier.net:3000";
    })
    .catch((error) => {
        console.log(error);
    })
}

export const requestTokens = (params) => {
    requestRefreshToken(params);
}
