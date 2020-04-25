import React, {useState, useEffect} from 'react';
import {useLocation} from 'react-router-dom'

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(() => {
        let queryArray = location.search.substring(1).split("&")
        const newParams = {}
        queryArray.forEach((term) => {
            const pair = term.split("=");
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        })
        return newParams;
    });

    const requestAccessToken = () => {
        // send fetch request for access token
        // once this has completed:
        window.location.href = "http://local.courier.net:3000"
    }

    const requestRefreshToken = () => {
        const url = `http://local.courier.net:8080/api/auth/token` +
                    `?client_id=${params.client_id}` + 
                    `&state=${params.state}` +
                    `&code=${params.code}` +
                    `&redirect_uri=${params.redirect_uri}` +
                    `&code_verifier=${params.code_verifier}` +
                    `&grant_type=authorization_code`
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
            requestAccessToken();
        })
        .catch((error) => {
            console.log(error);
        })
    }

    useEffect(() => {
        requestRefreshToken();
    }, [])

    return(
        <div></div>
    );

}

export default CallbackPage;