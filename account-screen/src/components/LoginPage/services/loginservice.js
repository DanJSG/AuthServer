import { generateCodeChallenge, generateState } from "../../../services/challengeservice";
import { buildQueryStringFromObject } from "../../../services/querystringmanipulator";

export const checkLoginForm = (email, password) => {
    if (email === undefined || email === null || email === "") {
        return "Please enter an email address.";
    }
    if (password === undefined || password === null || password === "") {
        return "Please enter a password.";
    }
    return null;
}

const checkParams = (params) => {
    const paramNames = [
        "code_challenge",
        "response_type",
        "client_id",
        "redirect_uri",
        "state",
        "code_challenge_method"
    ]
    let success = true;
    paramNames.forEach(paramName => {
        if (!params.hasOwnProperty(paramName))
            success = false;
    })
    return success;
}

export const sendLoginRequest = async (email, password, params) => {
    if (!checkParams(params)) {
        const state = generateState();
        const challenge = generateCodeChallenge();
        sessionStorage.setItem("state", state);
        sessionStorage.setItem("code_verifier", challenge.code_verifier);
        params = {
            code_challenge: challenge.code_challenge,
            response_type: "code",
            state: state,
            redirect_uri: process.env.REACT_APP_REDIRECT_URI,
            code_challenge_method: challenge.code_challenge_method
        }
    }
    const path = buildQueryStringFromObject("/api/v1/authorize", params);
    const url = "http://local.courier.net:8090" + path;
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
            if (response.status !== 200)
                throw Error("The email and password combination you have entered is incorrect.");
            return response.json();
        })
        .then((json) => {
            if (!json)
                throw Error("An error occurred whilst the server was processing your request. Please refresh the page and retry.");
            return {
                code: json.code,
                error: null,
                params: params
            };
        })
        .catch((error) => {
            return {
                code: null,
                error: error.message,
                params: null
            };
        })
}
