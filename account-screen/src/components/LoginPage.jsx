import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom'
import {generateCodeChallenge, generateState} from './codeGen'

// local.courier.net:3000/oauth2/authorize?audience=courier&scope=name+email&response_type=code&client_id=ThpDT2t2EDlO&redirect_uri=http://local.courier.net:3000/oauth2/auth_callback

function LoginPage() {

    const location = useLocation();
    const [authCode, setAuthCode] = useState(null);
    const [formError, setFormError] = useState(null);
    const [params] = useState(() => {
        let queryArray = location.search.substring(1).split("&")
        const newParams = {}
        queryArray.forEach((term) => {
            const pair = term.split("=");
            newParams[pair[0]] = pair[1].replace(/[+]/g, " ");
        })
        return newParams;
    });
    const [state] = useState(generateState());
    const [codeChallenge] = useState(generateCodeChallenge());

    const checkForm = (email, password) => {
        if(email === undefined || email === null || email === "") {
            setFormError("Please enter an email address.");
            return false;
        }
        if(password === undefined || password === null || password === "") {
            setFormError("Please enter a password.");
            return false;
        }
        return true;
    }

    const sendLoginRequest = (email, password) => {
        console.log(state);
        console.log(codeChallenge.code_challenge);
        const url = `http://local.courier.net:8080/api/auth/authorize?code_challenge=${codeChallenge.code_challenge}&response_type=${params.response_type}&client_id=${params.client_id}&redirect_uri=${params.redirect_uri}&state=${state}`;
        const credentials = JSON.stringify({credentials: btoa(JSON.stringify({email: email, password: password}))})
        fetch(url, {
            method: "POST",
            body: credentials,
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then((response) => {
            if(response.status !== 200) {
                console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
                console.log(response);
                setFormError("The email address and password combination you have entered is incorrect.");
                return;
            }
            return response.json();
        })
        .then((json) => {
            if(!json) {
                return;
            }
            console.log("Auth code is: " + json.code);
            setAuthCode(json.code);
        })
        .catch((error) => {
            console.log(`Fetch error: ${error}`);
            setFormError("An error when contacting the authorization server.");
        })
    }

    const handleLogin = (e) => {
        e.preventDefault();
        setFormError(null);
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        if(checkForm(email, password)) {
            sendLoginRequest(email, password);
        }
    }

    const redirectToCallback = () => {
        // const uriString = `${params.redirect_uri}?code=${authCode}&state=${state}`;
        // console.log(uriString);
        window.location.href = `${params.redirect_uri}?code=${authCode}&state=${state}&client_id=${params.client_id}&redirect_uri=${params.redirect_uri}&code_verifier=${codeChallenge.code_verifier}`;
    }

    return(
        <div className="container-fluid inherit-height">
            <div className="row align-items-center justify-content-center inherit-height">
                <div className="col-3">
                    {authCode === null ? 
                        <div>
                            <h1 className="mb-4">Sign In</h1>
                            <form onSubmit={handleLogin}>
                                <div className="form-group">
                                    <input className="form-control input-hover" type="email" name="email" placeholder="Email address"></input>
                                </div>
                                <div className="form-group">
                                    <input className="form-control input-hover" type="password" name="password" placeholder="Password"></input>
                                    {formError && <label className="form-text">{formError}</label>}
                                </div>
                                <div className="form-group">
                                    <button id="signInButton" className="btn btn-primary">Sign In</button>
                                </div>
                                <div className="form-group">
                                <label className="form-text">
                                    Not got an account yet? <Link className="link text-decoration-none" to="/sign-up">Sign up here.</Link>
                                </label>
                                </div>
                            </form>
                        </div>
                        :
                        redirectToCallback()
                    }
                </div>
            </div>
        </div>
    );
}

export default LoginPage;