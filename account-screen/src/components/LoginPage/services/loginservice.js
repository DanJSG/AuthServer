export const checkLoginForm = (email, password) => {
    if(email === undefined || email === null || email === "") {
        return "Please enter an email address.";
    }
    if(password === undefined || password === null || password === "") {
        return "Please enter a password.";
    }
    return null;
}

export const sendLoginRequest = async (email, password, params, codeChallenge, state) => {
    const url = `http://local.courier.net:8080/api/v1/authorize` + 
                `?code_challenge=${codeChallenge.code_challenge}` + 
                `&response_type=${params.response_type}` + 
                `&client_id=${params.client_id}` + 
                `&redirect_uri=${params.redirect_uri}` + 
                `&state=${state}` + 
                `&code_challenge_method=${codeChallenge.code_challenge_method}`;
    const credentials = JSON.stringify({
        credentials: btoa(JSON.stringify({
            email: email,
            password: password
        }))
    })
    return await fetch(url, {
        method: "POST",
        body: credentials,
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then((response) => {
        if(response.status !== 200) {
            return {
                code: null,
                error: "The email address and password combination you have entered is incorrect."
            };
        }
        return response.json();
    })
    .then((json) => {
        if(!json) {
            return {
                code: null,
                error: "An error occurred whilst the server was processing your request. Please refresh the page and retry."
            };
        }
        return {
            code: json.code,
            error: null
        };
    })
    .catch((error) => {
        return {
            code: null,
            error: "An error occurred when contacting the authorization server."
        };
    })
}
